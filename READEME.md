# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.3/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.5.3/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.5.3/reference/web/servlet.html)
* [Spring Session for JDBC](https://docs.spring.io/spring-session/reference/)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.5.3/reference/data/sql.html#data.sql.jpa-and-spring-data)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)
* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

### Docker Postgres Start

一行啟動 PostgreSQL 的指令：
```
docker run --name my-postgres -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin -e POSTGRES_DB=calendar -p 5432:5432 -d postgres:15
```

| 參數                            | 說明                                  |
| ----------------------------- | ----------------------------------- |
| `--name my-postgres`          | 容器名稱                                |
| `-e POSTGRES_USER=admin`     | 建立的 PostgreSQL 使用者名稱                |
| `-e POSTGRES_PASSWORD=admin` | 使用者密碼                               |
| `-e POSTGRES_DB=calendar`         | 預設建立的資料庫                            |
| `-p 5432:5432`                | 映射本機的 5432 port（PostgreSQL 預設 port） |
| `-d`                          | 背景執行（detached mode）                 |
| `postgres:15`                 | 使用 PostgreSQL 15 的官方映像              |

啟動後測試連線，輸入以下指令：
```
psql -h localhost -U admin -d calendar
```

### CORS Error
必須讓你的後端（假設是 Spring Boot / Node.js / Express / Java / Python 等）回傳以下 Header：
```
Access-Control-Allow-Origin: http://localhost:4200
```
後端允許跨網域(http://localhost:4200)的要求
```
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class GlobalCorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 所有路徑
                .allowedOrigins("http://localhost:4200") // 允許 Angular 前端
                .allowedMethods("*") // GET, POST, OPTIONS, ...
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
```

如果設定後發送要求回傳 `403`:
- 瀏覽器在發送「正式請求」前，會先發送一個 預檢請求（OPTIONS 請求） 來詢問伺服器是否允許跨域。如果後端沒處理這個預檢請求，它就會回傳 403。