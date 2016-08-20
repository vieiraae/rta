# Real-Time Analytics

>This projects leverages HTML5 and websockets to provide lightweight Analytics in Real-Time.

The server side is thin and based on a data grid architecture. The current implementation uses Coherence and Weblogic as back-end components. Coherence provides several mechanisms to ingest data in the Grid: REST API and SDK’s in Java, .Net & C++. GoldenGate Hotcache may be a good mechanism to ingest data from OLTP DB or Big Data repos.

![Real-Time Analytics][logo]
[logo]: https://raw.githubusercontent.com/vieiraae/rta/master/docs/images/rta-showcase.png "Real-Time Analytics"
* Built to be light & fast
* Coherence provides the (portable) object storage and live events
* No need for a database (optional to load baselines or other relational data)
* Weblogic provides the management layer (config, monitoring, wlst, etc.)
* 0%  server side includes (except the login page)
* Built to be production ready!
* Works great on the cloud...

> The application was built to be simple so that it’s easy to extend and reuse. Feel free to use/change the code according your needs and if possible contribute to the project with new features and improvements.

##Demo Enviroment (hosted on Microsoft Azure)
https://vieiraae.github.io/rta/demo
**_Demo instructions_**
* Login with the user `alex` and the password `welcome1` (already set)
* Hit the button on the top right corner to Play Simulations. The charts should wake and start to display random numbers.
* On the left top corner hit the open button and change the dashboards.
* Hit the edit button and change the dashboards. Saving has immediate effect (no need to refresh).
* Now open another browser (for example with a mobile phone) and login with the user `anna` and password `welcome1`
* Test the chat between users and give it a try with the push notifications.
* Send javascript commands (for testing purposes only) for example with: alert('hello');

##Architecture
![RTA Architecture][architecture]
[architecture]: https://raw.githubusercontent.com/vieiraae/rta/master/docs/images/rta-architecture.png "RTA Architecture"
The front-end uses Bootstrap 3, JQuery and different chart libraries (chartjs, d3s, c3, etc.). It renders well on mobile and desktop interfaces. The layout is based on the AdminLTE theme and the dashboard authoring tool is based on the Ace editor.

##Recommend Installation and Deployment for Dev Environment:
* Create a base directory for the project and navigate to the directory with a command line 
* Get the code from https://github.com/vieiraae/rta/archive/master.zip and extract into the directory
* Or use git (preferred) by executing those commands: `git init`; `git remote add origin https://github.com/vieiraae/rta.git`; `git pull origin master`
* Install JDeveloper Studio 12.2.1 (it also works on 12.1.3 with some minor changes)
* Start IntegratedWeblogicServer and wait until is in RUNNING state
* Open the Application by browsing to the `RealTimeAnalyticsApp.jws` file
* Open the EJBModule Project Properties and select the Libraries and Classpath. Set the Json library to point to a valid path where is the oracle_common/module/org.glassfish.javax.json.jar file.
* Open the weblogic.xml file under WebModule/Web Content/WEB-INF and set the local-path to point to the RealTimeAnalyticsDemo folder (must be the full path).
* Open the DashboardManagerService.java file under WebModule/Application Sources/rta/resources and set the `DASHBOARDS_VIRTUAL_DIR_PATH` variable with the same full path (a more elegant mechanism should be used in the future for this step).
* Open the Application menu and hit deploy -> rta_app
* In the next dialog choose to deploy to the Application Server hit next to select the IntegratedWeblogicServer and then finish. 
* Check the deployment log to ensure that the deployment finished successfully.
* Point the browser to the URL that shows in the end of the deployment. By default: http://localhost:7101/rta
* Enter the user weblogic and the password that you used when the IntegratedWeblogicServer domain was created

**_Each dashboard has the following files_**
* _menu.html_ sets the pages on the dashboard or external references. 
* _dashboard.html_ defines the layout with the dashboard elements (charts and other html5 components)
* _dashboard.js_ defines the logic to update the dashboard elements when values in the data grid are added, changed or deleted

##Recommend Installation and Deployment for Test and Prod Environments:
![RTA Topology][topology]
[topology]: https://raw.githubusercontent.com/vieiraae/rta/master/docs/images/rta-topology.png "RTA Topology"

* Download the “Generic Installer for Oracle WebLogic Server and Oracle Coherence”. Version 12.2.1 (it also works on 12.1.3 with some minor changes).
* Proceed with the installation
* Create a new domain with the weblogic coherence cluster extension template and start the admin and the managed servers
* Copy the dashboards folder to the path that was previously configured
* Open the weblogic console and deploy the rta_app.ear file (the rta_app.ear may be generated in JDeveloper)
* Add users on the weblogic console and have fun
* The Weblogic Logging mechanism may be used (WLDF Query: MESSAGE LIKE 'RTA:%‘)

##Project Structure
![RTA Project Structure][project]
[project]: https://raw.githubusercontent.com/vieiraae/rta/master/docs/images/rta-project.png "RTA Project Structure"
