# ceuMassMediator
Source-code of metabolite annotation tool called CEU Mass Mediator. This database is accessible at [http://ceumass.eps.uspceu.es/](http://ceumass.eps.uspceu.es/) while the data is provided at [https://github.com/albertogilf/ceuMassMediator/CMMAspergillusDB](https://github.com/albertogilf/ceuMassMediator/CMMAspergillusDB)

# Deploying

It is necessary to access the full CMM database of CEU. Request access to alberto.gilf@gmail.com and import it in any RDBMS.
Once you have access to the database, it is needed a data-source from the app server used with the credentials provided and the subsequent configuration in the J2EE project. If you need any help, do not hesitate to write us or open a pull request.

# Dependencies 
A middleware to connect the RDBMS with the application should be included and configured. mysql-connector-java-8.0.21 is used in both development and production environment. 
The libraries under the folder \web\WEB-INF\lib should be included in the project

