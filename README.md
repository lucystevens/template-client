![Develop Status][workflow-badge-develop]
![Main Status][workflow-badge-main]
![Version][version-badge]  

# template-client
**template-client** is an template repository for creating a new client library for services
made with [server-lib][server-lib-repo]. It is closely linked with the [template-api][template-api-repo]
project.

This project uses Google's [gson][gson-repo] library for Java serialisation and de-serialisation, and Square's [OkHttp][okhttp-repo] library for making http requests.

## Creating a project using the template
 - Create a new repository using this as a template on GitHub
 - Checkout the new repository and change marked fields in the `pom.xml` file.
 - Change necessary parts of this README, and remove this section
 - Create necessary models for the API, replacing `Example`
 - Create appropriate service(s) and interface(s) for the API, replacing `TemplateApi` and `TemplateApiClient`. Client classes can extend `AbstractApiClient` for useful helper methods.

## Installation

Install the latest version of template-client using Maven:

```	
<dependency>
	<groupId>uk.co.lukestevens</groupId>
	<artifactId>template-client</artifactId>
	<version>1.0.0-SNAPSHOT</version>
</dependency>
```

If not using server-lib, you'll also need to specify the transitive base-lib dependency

```	
<dependency>
	<groupId>uk.co.lukestevens</groupId>
	<artifactId>base-lib</artifactId>
	<version>2.1.0</version>
</dependency>
```

### Github Packages Authentication
Currently public packages on Github require authentication to be installed by Maven. Add the following repository to your project's `.m2/settings.xml`

```
<repository>
	<id>github-lukecmstevens</id>
	<name>GitHub lukecmstevens Apache Maven Packages</name>
	<url>https://maven.pkg.github.com/lukecmstevens/packages</url>
	<snapshots><enabled>true</enabled></snapshots>
</repository>
```

For more information see here: [Authenticating with Github packages][gh-package-auth]

## Usage
### Configuration
The TemplateApiClient requires the `template.api.address` to be set in the config passes to the client. This defines the domain-level address the client will point at (e.g. `localhost:8000` or `template.server.com`)

### Initialising the client
Other than the config specified above, the client requires a Google gson instance and an OkHttpClient instance:

```
TemplateApi api = new TemplateApiClient(config, gson, httpClient);
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

New features, fixes, and bugs should be branched off of develop.

Please make sure to update tests as appropriate.

## License
[MIT][mit-license]

[gh-package-auth]: https://docs.github.com/en/free-pro-team@latest/packages/guides/configuring-apache-maven-for-use-with-github-packages#authenticating-to-github-packages
[workflow-badge-develop]: https://img.shields.io/github/workflow/status/lukecmstevens/template-client/publish/develop?label=develop
[workflow-badge-main]: https://img.shields.io/github/workflow/status/lukecmstevens/template-client/release/main?label=main
[version-badge]: https://img.shields.io/github/v/release/lukecmstevens/template-client
[mit-license]: https://choosealicense.com/licenses/mit/
[server-lib-repo]: https://github.com/lukecmstevens/server-lib
[template-api-repo]: https://github.com/lukecmstevens/template-api
[gson-repo]: https://github.com/google/gson
[okhttp-repo]: https://github.com/square/okhttp
