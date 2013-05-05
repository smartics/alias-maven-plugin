<?xml version="1.0" encoding="UTF-8"?>
<!--

    Copyright 2012-2013 smartics, Kronseder & Reiner GmbH

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:xs="http://www.w3.org/2001/XMLSchema"
  version="1.0">
  	<xsl:output
		   method="xml"
		   indent="yes"
		   omit-xml-declaration="no"
		   media-type="text/xml"/>


	<xsl:template match="/">
		<document>
			<properties>
				<title>The alias XSD</title>
			</properties>
			<body>
				<section name="The alias XSD">
					<p>
					  XSD for download (<a href="download/alias.xsd">Download alias.xsd</a>).
					</p>
					<source><xsl:text disable-output-escaping="yes">&lt;![CDATA[</xsl:text>
			  		<xsl:apply-templates select="xs:schema"/>
			  		<xsl:text disable-output-escaping="yes">]]&gt;</xsl:text>
			    </source>
			  </section>
			</body>
		</document>
	</xsl:template>

  <xsl:template match="xs:schema">
    <xsl:copy-of select="."/>
  </xsl:template>
</xsl:stylesheet>
