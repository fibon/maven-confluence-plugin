Available goals:
================
* **addpage** - adds new page (with attachments) and optionally exports it
* **addblogentry** - adds new blog entry
* **addcomment** - adds comment to existing page
* **addattachment** - adds attachment to existing page
* **exportpage** - exports existing page to several supported formats

Example plugin definition:
==========================
    <plugin>
        <groupId>com.fibon.maven.plugins</groupId>
        <artifactId>maven-confluence-plugin</artifactId>
        <version>0.1-SNAPSHOT</version>
        <configuration>
            <serverId>confluence-server</serverId>
            <url>https://confluence.example.org/confluence/ </url>
        </configuration>
    </plugin>

Example _addpage_ goal configuration:
-------------------------------------
    <configuration>
        <parent>
            <space>Example Space</space>
            <title>Parent page name</title>
        </parent>
        <pageTitle>Hello world</pageTitle>
        <inputFile>${basedir}/pagetemplate.txt</inputFile>
        <outputFile>${basedir}/HelloWorld.pdf</outputFile>
        <attachments>
            <value>${basedir}/image.gif</value>
            <value>${basedir}/document.doc</value>
        </attachments>
    </configuration>

Example _addblogentry_ goal configuration:
------------------------------------------
    <configuration>
        <space>Example space</space>
        <entryTitle>My First Post</entryTitle>
        <entryFile>${basedir}/blogentry.txt</entryFile>
    </configuration>

Example _addcomment_ goal configuration:
----------------------------------------
    <configuration>
        <page>
            <space>Example space</space>
            <title>My comments</title>
        </page>
        <commentBody>${basedir}/comment.txt</commentBody>
    </configuration>

Example _addattachment_ goal configuration:
-------------------------------------------
    <configuration>
        <page>
            <space>Example space</space>
            <title>My attachments</title>
        </page>
        <comment>Add attachment example</comment>
        <attachments>
            <value>${basedir}/document.pdf</value>
            <value>${basedir}/picture.png</value>
        </attachments>
    </configuration>

Example _exportpage_ goal configuration:
----------------------------------------
    <configuration>
        <page>
            <space>Example space</space>
            <title>My page</title>
        </page>
        <outputFile>${basedir}/MyPage.pdf</outputFile>
    </configuration>
