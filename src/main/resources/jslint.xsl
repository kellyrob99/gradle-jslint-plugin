<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="html" indent="yes"/>
    <xsl:decimal-format decimal-separator="." grouping-separator=","/>

    <xsl:key name="files" match="file" use="@name"/>

    <xsl:template match="jslint">
        <html>
            <head>
                <style type="text/css">
                    body {
                    font: normal arial, helvetica, sanserif;
                    }
                    th {
                    text-align: left;
                    font-weight: bold;
                    }
                    table {
                    width: 100%;
                    }
                    table, th, td {
                    border: 1px solid black;
                    }
                    table {
                    border-collapse: collapse;
                    }
                    tr.a {
                    background-color: lightgray;
                    }
                </style>
            </head>
            <body>
                <a name="top"></a>
                <h1>JSLint Report</h1>

                <!-- Summary part -->
                <xsl:apply-templates select="." mode="summary"/>

                <!-- Package List part -->
                <xsl:apply-templates select="." mode="filelist"/>

                <!-- For each file create its part -->
                <xsl:apply-templates
                        select="file[@name and generate-id(.) = generate-id(key('files', @name))]"/>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="jslint" mode="summary">
        <xsl:variable name="fileCount"
                      select="count(file[@name and generate-id(.) = generate-id(key('files', @name))])"/>
        <xsl:variable name="errorCount" select="count(file/issue)"/>
        <xsl:value-of select="$fileCount"/> file(s) analyzed;
        <xsl:value-of select="$errorCount"/> issue(s) found.
    </xsl:template>

    <xsl:template match="jslint" mode="filelist">
        <h3>Files</h3>
        <table>
            <tr>
                <th>Name</th>
                <th>Issues</th>
            </tr>
            <xsl:for-each
                    select="file[@name and generate-id(.) = generate-id(key('files', @name))]">
                <xsl:sort data-type="number" order="descending"
                          select="count(key('files', @name)/issue)"/>
                <xsl:variable name="currentName" select="@name"/>
                <xsl:variable name="errorCount" select="count(../file[@name=$currentName]/issue)"/>
                <tr>
                    <xsl:call-template name="alternated-row"/>
                    <td>
                        <xsl:if test="$errorCount = 0">
                            <xsl:value-of select="@name"/>
                        </xsl:if>
                        <xsl:if test="$errorCount &gt; 0">
                            <a>
                                <xsl:attribute name="href">
                                    <xsl:value-of
                                            select="concat('#f-', translate(@name, '\', '/'))"></xsl:value-of>
                                </xsl:attribute>
                                <xsl:value-of select="@name"/>
                            </a>
                        </xsl:if>
                    </td>
                    <td>
                        <xsl:value-of select="$errorCount"/>
                    </td>
                </tr>
            </xsl:for-each>
        </table>
    </xsl:template>

    <xsl:template match="file">
        <xsl:variable name="errorCount" select="count(./issue)"/>
        <xsl:if test="$errorCount > 0">
            <a>
                <xsl:attribute name="name">
                    <xsl:value-of select="concat('f-', translate(@name, '\', '/'))"></xsl:value-of>
                </xsl:attribute>
            </a>
            <h3>File
                <xsl:value-of select="@name"/>
            </h3>

            <table>
                <tr>
                    <th>Line</th>
                    <th>Column</th>
                    <th>Issue</th>
                    <th>Code</th>
                </tr>
                <xsl:for-each select="key('files', @name)/issue">
                    <tr>
                        <xsl:call-template name="alternated-row"/>
                        <td>
                            <xsl:value-of select="@line"/>
                        </td>
                        <td>
                            <xsl:value-of select="@char"/>
                        </td>
                        <td>
                            <xsl:value-of select="@reason"/>
                        </td>
                        <td>
                            <code>
                                <xsl:value-of select="@evidence"/>
                            </code>
                        </td>
                    </tr>
                </xsl:for-each>
            </table>
            <a href="#top">Back to top</a>
        </xsl:if>
    </xsl:template>

    <xsl:template name="alternated-row">
        <xsl:attribute name="class">
            <xsl:if test="position() mod 2 = 1">a</xsl:if>
            <xsl:if test="position() mod 2 = 0">b</xsl:if>
        </xsl:attribute>
    </xsl:template>
</xsl:stylesheet>
