/*
MIT License

Copyright (c) 2016 Alexandre Vieira https://github.com/vieiraae/rta

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
var dashboardManager = {
	activeDashboard: null,
	dashboardList: {},
	aceEditor: null,
	selectedFile: null,
	editorSessions: {},
	jsTreeInstance: null,
	unsavedChanges: false,
    initialize: function() {
		$.ajax({
			url: "dashboards/dashboards.json",
			dataType: "json",
			mimeType: "text/plain",
			cache: false,
			success: function(jsonData){
				if (jsonData.length > 0) {	
					var index = 0;
					jsonData.forEach( function (arrayItem) {
						dashboardManager.dashboardList[index] = arrayItem;
						index++;
					});
					dashboardManager.activeDashboard = dashboardManager.readCookie('rta_dashboard');
					if (dashboardManager.activeDashboard)
						dashboardManager.open(dashboardManager.activeDashboard);
					else
						dashboardManager.open(dashboardManager.dashboardList[0].id);	
				}
			},
			error: function( data, textStatus, jqxhr ) {
				rta.modal('Failed to load dashboard list', 'Error loading the dashboards.json file. <br/>Reason: ' + textStatus + '. <br/>' + jqxhr);
			}				
		});  		
    },
	browseDialog: function() {		
		if (dashboardManager.unsavedChanges) {
			$('#modal-confirm-browse').modal('show');
		} else {
			dashboardManager.browse();
		}	
	},
	browse: function() {
		dashboardManager.closeFile();	

		$.ajax({
			url: "dashboards/dashboards.json",
			dataType: "json",
			mimeType: "text/plain",
			cache: false,
			success: function(jsonData){
				if (jsonData.length > 0) {	
					var index = 0;
					jsonData.forEach( function (arrayItem) {
						dashboardManager.dashboardList[index] = arrayItem;
						index++;
					});
				}
				$("#modal-title").html('<i class="fa fa-folder-open-o"></i> Choose the Dashboard');
				var body = '';
				for (var key in dashboardManager.dashboardList) {
					if (dashboardManager.dashboardList.hasOwnProperty(key)) {
						item = dashboardManager.dashboardList[key];
						body = body + '<a href="#" onclick="dashboardManager.open(\''+item.id+'\');"><div class="info-box bg-aqua"><span class="info-box-icon"><i class="' + item.icon + '"></i></span><div class="info-box-content"><span class="info-box-text">' + item.name + '</span><div class="progress"><div class="progress-bar" style="width: 100%"></div></div><small>' + item.description + '</small></div></div></a>';
					}
				}
				$("#modal-body").html(body);
				$("#modal-dialog").modal('show');
			},
			error: function( data, textStatus, jqxhr ) {
				rta.modal('Failed to load dashboard list', 'Error loading the dashboards.json file. <br/>Reason: ' + textStatus + '. <br/>' + jqxhr);
			}				
		});  		
	},	
	open: function(dashboardName) {		
		$("#dashboard-edit").show();	
		$.ajax({
			url: "dashboards/" + dashboardName + "/menu.html",
			dataType: "html",
			cache: false,
			success: function(menuData) {
				$.ajax({
					url: "dashboards/" + dashboardName + "/dashboard.html",
					dataType: "html",
					cache: false,
					success: function(dashboardData) {
						$.ajax({
							url: "dashboards/" + dashboardName + "/dashboard.js",
							dataType: "script",
							cache: false,
							success: function(jsData) {
								$("#menu-fragment").html(menuData);
								dashboardManager.setMenu();
								$("#dashboard").html(dashboardData);
								$.AdminLTE.layout.fix();
								dashboardManager.activeDashboard = dashboardName;
								document.cookie = "rta_dashboard=" + dashboardManager.activeDashboard + "; path=/";								
								dashboard.initialize();												
							},
							error: function( data, textStatus, jqxhr ) {
								rta.modal('Failed to load the dashboard', 'Error loading the dashboard.js file for the ' + dashboardName + ' dashboard. <br/>Reason: ' + textStatus + '. <br/>' + jqxhr);
							}
						});
					},
					error: function( data, textStatus, jqxhr ) {
						rta.modal('Failed to load the dashboard', 'Error loading the dashboard.html file for the ' + dashboardName + ' dashboard. <br/>Reason: ' + textStatus + '. <br/>' + jqxhr);
					}					
				});				
			},
			error: function( data, textStatus, jqxhr ) {
				rta.modal('Failed to load the dashboard', 'Error loading the menu.html file for the ' + dashboardName + ' dashboard. <br/>Reason: ' + textStatus + '. <br/>' + jqxhr);
			}			
		});		
	},
	edit: function() {
		$("#dashboard-edit").hide();
		dashboardManager.selectedFile = null;
		dashboardManager.editorSessions = {};
		
		$.ajax({
			url: "admin/dashboard-manager/menu.html",
			dataType: "html",
			cache: false,
			success: function(menuData) {
				$.ajax({
					url: "admin/dashboard-manager/dashboard.html",
					dataType: "html",
					cache: false,
					success: function(dashboardData) {
						$("#menu-fragment").html(menuData);
						dashboardManager.setMenu();
						$("#dashboard").html(dashboardData);
						$("#dashboard-name").html('&lt;editing/&gt;');
						$.AdminLTE.layout.fix();
						
						$('#dashboards-refresh-button').on("click", function () {
							dashboardManager.refreshDashboardsTreeDialog();
						});						
						$('#dashboards-save-all').on("click", function () {
							dashboardManager.saveAllFiles();
						});	
						$('#dashboard-save').on("click", function () {
							dashboardManager.saveFile(dashboardManager.selectedFile);
						});							
						$('#dashboard-close').on("click", function () {
							dashboardManager.closeFileDialog();
						});							
						$('a[id^=theme-]').click(function () {
							dashboardManager.aceEditor.setTheme("ace/theme/" + this.id.substr(this.id.lastIndexOf("theme-") + 6));
						});
						$(window).on('beforeunload', function() {
							if (dashboardManager.unsavedChanges) return "You have unsaved changes!";
						});						
						
						$("#file-editor-panel").hide();
						dashboardManager.refreshDashboardsTree();
	
						dashboardManager.aceEditor = ace.edit("file-editor");
						dashboardManager.aceEditor.commands.addCommand({
							name: 'saveFile',
							bindKey: {
								win: 'Ctrl-S',
								mac: 'Command-S',
								sender: 'editor|cli'
							},
							exec: function(env, args, request) {
								dashboardManager.saveFile(dashboardManager.selectedFile);
							}
						});		
						dashboardManager.aceEditor.setTheme("ace/theme/chrome");						
					},
					error: function( data, textStatus, jqxhr ) {
						rta.modal('Failed to load the dashboard manager', 'Error loading the dashboard.html file for the dashboard manager. <br/>Reason: ' + textStatus + '. <br/>' + jqxhr);
					}					
				});				
			},
			error: function( data, textStatus, jqxhr ) {
				rta.modal('Failed to load the dashboard', 'Error loading the menu.html file for the dashboard manager. <br/>Reason: ' + textStatus + '. <br/>' + jqxhr);
			}			
		});
	},
	refreshDashboardsTreeDialog: function() {
		if (dashboardManager.unsavedChanges) {
			$('#modal-confirm-refresh').modal('show');
		} else {
			dashboardManager.refreshDashboardsTree();
		}
	},
	refreshDashboardsTree: function() {
		dashboardManager.closeFile();
		$('#dashboard-save').prop('disabled', true);		
		dashboardManager.unsavedChanges = false;
		$('#dashboards-save-all').prop('disabled', (!dashboardManager.unsavedChanges));
		$('#dashboard-operations-dropdown').prop('disabled', dashboardManager.unsavedChanges);		
		dashboardManager.editorSessions = {};
		$('#dashboards-tree').jstree("destroy").empty();
		if (dashboardManager.editorSessions[dashboardManager.selectedFile] != null)
			dashboardManager.closeFile();
		$.ajax({
			url: '/rta/resources/dashboards/list',
			dataType: "json",
			cache: false,
			success: function(jstreeData) {
				// data format demo
				$('#dashboards-tree')
				.jstree({
					'core' : {
						'data' : jstreeData
					}	
				})
				.on("changed.jstree", function (e, data) {
					dashboardManager.openFile(data);
				});
				
			},
			error: function( data, textStatus, jqxhr ) {
				rta.modal('Failed to load the dashboards', 'Error loading the dashboards directory. <br/>Reason: ' + textStatus + '. <br/>' + jqxhr);
			}				
		});				
	},
	addDashboardDialog: function() {
		$('#modal-add-dashboard').modal('show');
	},	
	addDashboard: function() {
		$.ajax({
			url: '/rta/resources/dashboards/add?source=template&target=' + $("#modal-add-dashboard-id").val(),
			method: 'PUT',
			dataType: "json",
			cache: false,
			success: function(data) {
				if (data.status != "ok")
					rta.modal('Error', data.status);
				else
					dashboardManager.refreshDashboardsTree();
			},
			error: function( data, textStatus, jqxhr ) {
				rta.modal('Failed to add dashboard', 'Error adding new dashboard. <br/>Reason: ' + textStatus + '. <br/>' + jqxhr);
			}				
		});				
	},
	renameFileDialog: function() {
		if (dashboardManager.selectedFile != null) {
			$("#modal-rename-file-name").val(dashboardManager.selectedFile);
			$('#modal-rename-file').modal('show');
		}
	},	
	renameFile: function() {
		$.ajax({
			url: '/rta/resources/dashboards/file/rename?oldName=' + dashboardManager.selectedFile + '&newName=' + $("#modal-rename-file-name").val(),
			method: 'PUT',
			dataType: "json",
			cache: false,
			success: function(data) {
				if (data.status != "ok")
					rta.modal('Error', data.status);
				else
					dashboardManager.refreshDashboardsTree();
			},
			error: function( data, textStatus, jqxhr ) {
				rta.modal('Failed to rename file', 'Error renaming the file. <br/>Reason: ' + textStatus + '. <br/>' + jqxhr);
			}				
		});				
	},	
	deleteFileDialog: function() {
		if (dashboardManager.selectedFile != null) {
			$('#modal-delete-file-body').html('<p>Are you sure you want to permanently delete "' + dashboardManager.selectedFile + '"?</p>'); 
			$('#modal-delete-file').modal('show');
		}
	},	
	deleteFile: function() {
		$.ajax({
			url: '/rta/resources/dashboards/file/delete?file=' + dashboardManager.selectedFile,
			method: 'DELETE',
			dataType: "json",
			cache: false,
			success: function(data) {
				if (data.status != "ok")
					rta.modal('Error', data.status);
				else
					dashboardManager.refreshDashboardsTree();
			},
			error: function( data, textStatus, jqxhr ) {
				rta.modal('Failed to delete file', 'Error deleting the file. <br/>Reason: ' + textStatus + '. <br/>' + jqxhr);
			}				
		});				
	},	
	openFile: function(data) {
		if(data.selected.length) {
			if (data.instance.is_parent(data.selected[0])) {
				$("#file-editor-panel").hide();
				dashboardManager.selectedFile = data.instance.get_path(data.selected[0], '/');					
				var dashboardName = dashboardManager.selectedFile;			
				$.ajax({
					url: "dashboards/" + dashboardName + "/menu.html",
					dataType: "html",
					cache: false,
					success: function(menuData) {				
						$.ajax({
							url: "dashboards/" + dashboardName + "/dashboard.html",
							dataType: "html",
							cache: false,
							success: function(dashboardData) {
								$.ajax({
									url: "dashboards/" + dashboardName + "/dashboard.js",
									dataType: "script",
									cache: false,
									success: function(jsData) {
										$("#menu-preview-content").html(menuData);
										dashboardManager.setMenu();
										$("#dashboard-preview-panel").html(dashboardData);
										dashboardManager.activeDashboard = dashboardName;
										$.AdminLTE.layout.fix();
										dashboard.initialize();
										$("#dashboard-name").html('&lt;editing/&gt;');								
									},
									error: function( data, textStatus, jqxhr ) {
										rta.modal('Failed to load the dashboard', 'Error loading the dashboard.js file for the ' + dashboardName + ' dashboard. <br/>Reason: ' + textStatus + '. <br/>' + jqxhr);
									}
								});
							},
							error: function( data, textStatus, jqxhr ) {
								rta.modal('Failed to load the dashboard', 'Error loading the dashboard.html file for the ' + dashboardName + ' dashboard. <br/>Reason: ' + textStatus + '. <br/>' + jqxhr);
							}					
						});
					}
				});
				$("#menu-preview-panel").show();
				$("#dashboard-preview-panel").show();
			} else {
				$("#dashboard-preview-panel").hide();
				$("#menu-preview-panel").hide();
				dashboardManager.selectedFile = data.instance.get_path(data.selected[0], '/');
				var currentSession = {};
				if (dashboardManager.editorSessions.hasOwnProperty(dashboardManager.selectedFile)) {
					currentSession = dashboardManager.editorSessions[dashboardManager.selectedFile];
					if (currentSession != null) {
						dashboardManager.aceEditor.setSession(currentSession.session);
						$('#dashboard-save').prop('disabled', (!currentSession.changed));
						$('#opened-file-name').html(dashboardManager.selectedFile + (currentSession.changed?' *':''));						
					} else {
						dashboardManager.loadFile(data);
					}
				} else {				
					dashboardManager.loadFile(data);
				}
				$("#file-editor-panel").show();				
			}
		}
	},
	loadFile: function(data) {
		var fileExtension = dashboardManager.selectedFile.substr(dashboardManager.selectedFile.lastIndexOf('.')+1);
		var currentSession = {};
		$.ajax({
			url: "/rta/dashboards/" + dashboardManager.selectedFile,
			cache: false,
			success: function(fileData) {
				switch (fileExtension) {
					case "js":
						currentSession.session = ace.createEditSession(fileData, "ace/mode/javascript");
						break;
					case "txt":
						currentSession.session = ace.createEditSession(fileData, "ace/mode/text");
						break;										
					default:
						currentSession.session = ace.createEditSession(fileData, "ace/mode/" + fileExtension);
				}
				currentSession.session.on("change", function() {
					if (dashboardManager.editorSessions.hasOwnProperty(dashboardManager.selectedFile)) {
						var currentSession = dashboardManager.editorSessions[dashboardManager.selectedFile];
						if (!currentSession.changed) {
							var currentIcon = $("#dashboards-tree").jstree(true).get_icon(currentSession.selectedNode);
							$("#dashboards-tree").jstree(true).set_icon(currentSession.selectedNode, currentIcon + "-changed");
							currentSession.changed = true;
							$('#dashboard-save').prop('disabled', (!currentSession.changed));
							dashboardManager.unsavedChanges = true;
							$('#dashboards-save-all').prop('disabled', (!dashboardManager.unsavedChanges));		
							$('#dashboard-operations-dropdown').prop('disabled', dashboardManager.unsavedChanges);							
							dashboardManager.editorSessions[dashboardManager.selectedFile] = currentSession;
							$('#opened-file-name').html(dashboardManager.selectedFile + ' *');
						}
					}
				});
				currentSession.selectedNode = data.instance.get_node(data.selected[0]);
				currentSession.changed = false;													
				$('#dashboard-save').prop('disabled', (!currentSession.changed));									
				dashboardManager.editorSessions[dashboardManager.selectedFile] = currentSession;
				dashboardManager.aceEditor.setSession(currentSession.session);
				$('#opened-file-name').html(dashboardManager.selectedFile);
			}
		});	
	},
	closeFileDialog: function() {
		var currentSession = dashboardManager.editorSessions[dashboardManager.selectedFile];		
		if (currentSession != null && currentSession.changed) {
			$('#modal-save-changes').modal('show');
		} else {
			dashboardManager.closeFile();
		}
	},	
	closeFile: function() {
		var currentSession = dashboardManager.editorSessions[dashboardManager.selectedFile];		
		if (currentSession != null) {
			if (currentSession.changed) {
				currentSession.changed = false;
				dashboardManager.editorSessions[dashboardManager.selectedFile] = currentSession;		
				$('#dashboard-save').prop('disabled', (!currentSession.changed));
				var currentIcon = $("#dashboards-tree").jstree(true).get_icon(currentSession.selectedNode);
				$("#dashboards-tree").jstree(true).set_icon(currentSession.selectedNode, currentIcon.substr(0, currentIcon.lastIndexOf("-changed")));
				dashboardManager.unsavedChanges = false;
				for (var key in dashboardManager.editorSessions) {
					if (dashboardManager.editorSessions.hasOwnProperty(key)) {
						currentSession = dashboardManager.editorSessions[key];
						if (currentSession != null && currentSession.changed) {
							dashboardManager.unsavedChanges = true;								
						}
					}
				}
				$('#dashboards-save-all').prop('disabled', (!dashboardManager.unsavedChanges));
				$('#dashboard-operations-dropdown').prop('disabled', dashboardManager.unsavedChanges);
				dashboardManager.editorSessions[dashboardManager.selectedFile] = null;
			}
			try { $('#dashboards-tree').jstree(true).deselect_all(); } catch(err) { }; 
			$("#file-editor-panel").hide();
		}
		dashboardManager.selectedFile = null;
	},
	saveFile: function(file) {
		var currentSession = dashboardManager.editorSessions[file];		
		if (currentSession.changed) {
			$.ajax({
				url: '/rta/resources/dashboards/file/save?file=' + file + '&charset=utf-8',
				method: 'POST',
				contentType: "text/plain; charset=utf-8",
				data: currentSession.session.getValue(),
				success: function(data) {
					if (data.status != "ok")
						rta.modal('Error', data.status);
					else {
						currentSession.changed = false;
						dashboardManager.editorSessions[file] = currentSession;
						$('#dashboard-save').prop('disabled', (!currentSession.changed));
						var currentIcon = $("#dashboards-tree").jstree(true).get_icon(currentSession.selectedNode);
						$("#dashboards-tree").jstree(true).set_icon(currentSession.selectedNode, currentIcon.substr(0, currentIcon.lastIndexOf("-changed")));
						dashboardManager.unsavedChanges = false;
						for (var key in dashboardManager.editorSessions) {
							if (dashboardManager.editorSessions.hasOwnProperty(key)) {
								currentSession = dashboardManager.editorSessions[key];
								if (currentSession.changed) {
									dashboardManager.unsavedChanges = true;								
								}
							}
						}
						$('#dashboards-save-all').prop('disabled', (!dashboardManager.unsavedChanges));		
						$('#dashboard-operations-dropdown').prop('disabled', dashboardManager.unsavedChanges);
						$('#opened-file-name').html(dashboardManager.selectedFile);
					}
				},
				error: function( data, textStatus, jqxhr ) {
					rta.modal('Failed to save the file', 'Error saving the file content. <br/>Reason: ' + textStatus + '. <br/>' + jqxhr);
				}				
			});				
		}
	},
	saveAndCloseFile: function() {
		dashboardManager.saveFile(dashboardManager.selectedFile);
		dashboardManager.closeFile();
	},
	saveAllFiles: function() {
		for (var key in dashboardManager.editorSessions) {
			if (dashboardManager.editorSessions.hasOwnProperty(key)) {
				dashboardManager.saveFile(key);
			}
		}
	},
	setMenu: function() {
		$('a[name^=rta-page-]').click(function () {
			var divname = this.name;
			$("#"+divname).show("slow").siblings('div[id^=rta-page-]').hide("slow");
			$('li[id^=link-rta-page-]').removeClass();
			$("#link-" + divname).addClass("active");
		});	
	},
	readCookie: function(name) {
		var nameEQ = name + "=";
		var ca = document.cookie.split(';');
		for (var i = 0; i < ca.length; i++) {
			var c = ca[i];
			while (c.charAt(0) == ' ') c = c.substring(1, c.length);
			if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
		}
		return null;
	}	
};
