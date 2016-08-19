var dashboard = {
	charts: {},
	initialize: function() {
		$("#dashboard-name").html('Smart City');
		dashboard.buildDonutChart("donut-chart", "Aggregation Demo", 70);
		dashboard.buildGaugeChart("gauge-chart");
		dashboard.buildCombChart("comb-chart");
		dashboard.buildBarChart("bar-chart");
	},
	scalar: {
		update: function(data) {
		}
	},	
	kpi: {
		update: function(id, data) {
			if (id && data) {
				if (id == "kpi2") {
					var chart = dashboard.charts["gauge-chart"];
					chart.load({
					  columns: [[ 'data', parseInt(data.value) ]]
					});
				}
				if ($('#' + id).length) {
					$('#' + id).html(data.value);
					if (data.type) {
						var classType = '';
						switch (data.type)
						{
						   case 0:
								classType = "small-box bg-green";
								break;
						   case 1:
								classType = "small-box bg-yellow";
								break;
						   case 2:
								classType = "small-box bg-red";
								break;
						   default: classType = "";
						}			
						$('#' + id).parent().parent().removeClass().addClass(classType);
					}
				}
			}
		}
	},
	aggregation: {
		update: function(type, id, data) {
			if (type && id && data) {
			var result = [];
			for(var i in data)
				result.push([data[i].name, data[i].value]);	
			}
			var chart = dashboard.charts[id];
			chart.load({
			  columns: result
			});
		}
	},	
	buildDonutChart: function(elem, chartTitle, chartWidth) {
      var chart = c3.generate({
		bindto: '#' + elem,
        data: {
          columns: [],
          type : 'donut',
          order: null // set null to disable sort of data. desc is the default.
        },
        donut: {
          title: chartTitle,
          width: chartWidth
        }
      });
	  dashboard.charts[elem] = chart;
	},
	buildGaugeChart: function(elem) {
      var chart = c3.generate({
          bindto: '#' + elem,
        data: {
          columns: [
            [ 'data', 0 ]
          ],
          type: 'gauge',
          onmouseover: function (d, i) { console.log("onmouseover", d, i, this); },
          onmouseout: function (d, i) { console.log("onmouseout", d, i, this); },
          onclick: function (d, i) { console.log("onclick", d, i, this); },
        },
        gauge: {
          label: {
            format: function(value, ratio) {
              return value + '%';
            },
          show: true // to turn off the min/max labels.
          },
          min: 0, // 0 is default, //can handle negative min e.g. vacuum / voltage / current flow / rate of change
          max: 100, // 100 is default
          width: 39 // for adjusting arc thickness
        },
        color: {
          pattern: ['#60B044', '#F6C600', '#FF0000'], // the three color levels for the percentage values.
          threshold: {
            unit: 'percentage', // value or percentage
            max: 100, // 100 is default
            values: [30, 60, 90] // alternate first value is 'value'
          }
        }
      });	
	  dashboard.charts[elem] = chart;
	},
	buildCombChart: function(elem) {
	    var chart = c3.generate({
			bindto: '#' + elem,
			data: {
			  columns: [
				['data1', 30, 20, 50, 40, 60, 50],
				['data2', 200, 130, 90, 240, 130, 220],
				['data3', 300, 200, 160, 400, 250, 250],
				['data4', 200, 130, 90, 240, 130, 220],
				['data5', 130, 120, 150, 140, 160, 150],
				['data6', 90, 70, 20, 50, 60, 120],
			  ],
			  types: {
				data1: 'bar',
				data2: 'bar',
				data3: 'spline',
				data4: 'line',
				data5: 'bar',
				data6: 'area'
			  },
			  groups: [
				['data1','data2']
			  ]
			},
			axis: {
			  x: {
				type: 'categorized'
			  }
			}
		});
	  dashboard.charts[elem] = chart;
	},
	buildBarChart: function(elem) {
      var chart = c3.generate({
		bindto: '#' + elem,
        data: {
          columns: [
            ['data1', 103, 120, 110],
            ['data2', 213, 210, 214]
//           ['data1', 30, 200, 100, 400, 150, 250],
//           ['data2', 130, 100, 140, 200, 150, 50]
          ],
          type: 'bar',
        },
        axis: {
          x: {
            type: 'categorized',
			categories: ['Category 1', 'Category 2', 'Category 3']
          }
        },
        bar: {
          width: {
            ratio: 0.3,
//            max: 30
          },
        }
      });		
	  dashboard.charts[elem] = chart;
	}
};



