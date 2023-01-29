# Spring Boot Telegram Bot Starter
## About
Spring Boot starter for developing Telegram Bots in declarative approach.

## How to use
1. Add to your `pom.xml`:
```
<repositories>
    <repository>
        <id>spring-boot-telegram-bot-starter</id>
        <url>https://raw.githubusercontent.com/AlexWhiteCorp/spring-boot-telegram-bot-starter/mvn-repo/</url>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
```
2. Add dependency
```
<dependency>
    <groupId>com.alexcorp.springspirit</groupId>
    <artifactId>spring-boot-telegram-bot-starter</artifactId>
    <version>${version}</version>
</dependency>
```