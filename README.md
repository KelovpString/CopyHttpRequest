# Copy Http Request And Compare

## Copy GET/POST request to target machine and get the ab result to save.

### Introduction
If your spring project is undergoing refactoring or generating new middleware, but you have no effective means to confirm the correctness of the original query APIS of the system after accessing the middleware, At this time, you only need to use Copy Http RequestAndCompare,to confirm your updating.
Copy Http Request And Compare has the following features:
- Copy GET/POST Request by Aspect
- Compare the Request Result
- Save Result and produce the charts

<img src="https://kelovp-1252729674.cos.ap-chengdu.myqcloud.com/UTOOLS1589724893749.jpg"  alt="Perform"/>

### Documentation
See:[KelovpString CompareApi Doc](https://kelovp.tech/nostring/blog/1362/)

### Quick Start

##### 1.package or deploy compare-core
```
cd compare-core
mvn clean package
```
or add distributionManagement in pom and deploy:
```$xslt
mvn clean deploy
```
##### 2.Add Dependency
If your application is build in Maven, just add the following dependency in pom.xml
```$xslt
<groupId>com.kelovp</groupId>
<artifactId>compare-core</artifactId>
<version>0.0.1-SNAPSHOT</version>
```
##### 3.Add properties
You can see all properties in:com.kelovp.compare.core.conf.CompareApiConf

And add application name properties:com.kelovp.application.name

##### 4. Add pointcut to Method:
```$xslt
 @RequestMapping(value = "/query", method = RequestMethod.POST)
 @CompareApi(name = "Class#query",excludeFields={"code","message"})
 public String query(String queryId){}
```

##### 5. Add Mango Properties and Package compare-dashboard
You can see all properties in:com.kelovp.compare.dashboard.conf.CompareApiMangoProperties
```$xslt
cd compare-dashboard
mvn clean package 
```
##### 6. Deploy Dashboard
```$xslt
java -jar compare-dashboard.jar
```
##### 7. Deploy you application A&B

##### 8. Query Report:
![report.png](https://github.com/KelovpString/CopyHttpRequest/raw/master/photo/report.png)

### Depends on
Spring Boot 1.4.7(you can change to your version)

Alibaba FastJSON ~~1.2.56~~ 1.2.83

Apache HttpClient

### Performance impact
Use ThreadPoolTaskExecutor. All things depends on "futureTaskPool"'s properties.

The Pressure test:
See:[KelovpString CompareApi Doc](https://kelovp.tech/nostring/blog/1362/)

### Bugs and Feedback
For bug report, questions and discussions please submit GitHub Issues

Contact me: work@kelovp.tech

