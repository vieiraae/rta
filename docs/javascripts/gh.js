(function(window, document) {

  /** functions & constants */

  var JSON_TYPE = /json/;
  var README = /README/;
  var strapdown = 'http://strapdownjs.com/v/0.1/strapdown.js';
  var githubUrl = 'https://api.github.com/';

  function get(url, headers, callback) {
    if (typeof(headers) === 'function') {
      callback = headers;
      headers = null;
    }


    var req = new XMLHttpRequest();
    req.open('GET', url, true);

    if (headers) {
      var header;
      for (header in headers) {
        req.setRequestHeader(header, headers[header]);
      }
    }

    req.onreadystatechange = function() {
      var type = req.getResponseHeader('Content-Type');
      var data;

      if (req.readyState === 4) {
        if (req.status === 200) {
          if (JSON_TYPE.test(type)) {
            data = JSON.parse(req.responseText);
          } else {
            data = req.responseText;
          }
          callback(null, req, data);
        } else {
          callback(
            new Error('status: ' + req.status),
            req
          );
        }
      }
    };
    req.send(null);
  }

  function render(target, content) {
    target.innerHTML = content;

    var script = document.createElement('script');

    script.src = strapdown;
    script.type = 'text/javascript';

    document.body.appendChild(script);
  }

  /** variables */

  var xmp = document.getElementsByTagName('xmp')[0];
  var project = xmp.getAttribute('data-project');

  var fileUrl = githubUrl + 'repos/' + project;
  fileUrl += '/git/trees/master';

  function error() {
    render(xmp, 'cannot load README.md for project "' + project + '"');
  }

  function githubFile(url, callback) {
    get(url, { 'Accept': 'application/vnd.github-blob.raw' }, callback);
  }

  /** loads a list of files from github */
  get(fileUrl, function(err, xhr, data) {
    if (err) {
      return error();
    }

    var list = data.tree;
    var readme;
    var i = 0;
    var item;

    for (; i < list.length; i++) {
      item = list[i];
      if (item.path && README.test(item.path)) {
        githubFile(item.url, function(err, xhr, content) {
          if (err) {
            return error();
          }
          render(xmp, content);
        });
      }
    }

  });

  /** xhr */
}(window, document));