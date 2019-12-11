# Files Exchange Service - RESTful Web Service - Backend 

Spring version of Files Exchange Service 

## Getting Started

### Setting up a project

* Move into your projects directory: `cd ~/YOUR_PROJECTS_DIRECTORY`
* Edit file src/main/resources/application.properties
    * Set password in server.ssl.key-store-password
    * Set datasource in spring.datasource.url
    * Set username in spring.datasource.username
    * Set password in spring.datasource.password
    * Set key in security.signing-key
    * Set secret in security.jwt.client-secret
* Create keystore.jks in src/main/resources/

### Creating keystore.jks

Step by step example how to creating keystore.jks:

```
openssl pkcs12 -export -name tomcat -in server.crt -inkey server.key -out keystore.p12

keytool -importkeystore -destkeystore keystore.jks -srckeystore keystore.p12 -srcstoretype pkcs12 -alias tomcat
```


## Built With

* [Maven](https://maven.apache.org/) - Dependency Management

## Authors

* **Miroslaw Tyc** - <miras72@o2.pl>


## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
