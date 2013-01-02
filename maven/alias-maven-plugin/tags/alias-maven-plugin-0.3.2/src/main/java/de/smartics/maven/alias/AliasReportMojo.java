/*
 * Copyright 2012-2013 smartics, Kronseder & Reiner GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.smartics.maven.alias;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.MavenReportException;
import org.xml.sax.InputSource;

import de.smartics.maven.alias.domain.AliasesProcessor;
import de.smartics.maven.alias.report.AliasReportRenderer;
import de.smartics.maven.alias.report.ReportAliasCollector;

/**
 * Generates a report about the aliases defined in this project.
 *
 * @goal alias-report
 * @phase site
 * @description Generates a report about the aliases defined in this project.
 * @requiresProject
 * @threadSafe
 * @since 1.0
 */
public final class AliasReportMojo extends AbstractReportMojo
{
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The location of the alias definitions. The contents of the file is required
   * to use the alias XSD.
   *
   * @parameter default-value="${basedir}/src/main/resources/alias.xml"
   * @required
   * @since 1.0
   */
  private String aliasLocation;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.MavenReport#getName(java.util.Locale)
   */
  public String getName(final Locale locale)
  {
    return getBundle(locale).getString("report.name");
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.MavenReport#getDescription(java.util.Locale)
   */
  public String getDescription(final Locale locale)
  {
    return getBundle(locale).getString("report.description");
  }

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.MavenReport#getOutputName()
   */
  public String getOutputName()
  {
    return "alias-report";
  }

  // --- business -------------------------------------------------------------

  /**
   * {@inheritDoc}
   *
   * @see org.apache.maven.reporting.AbstractMavenReport#executeReport(java.util.Locale)
   */
  @Override
  protected void executeReport(final Locale locale) throws MavenReportException
  {
    final Sink sink = getSink();
    final ResourceBundle messages = getBundle(locale);

    final InputSource source = createSource();
    final AliasesProcessor processor = createProcessor(source);
    final ReportAliasCollector collector = new ReportAliasCollector();
    processor.process(collector);

    final AliasReportRenderer renderer =
        new AliasReportRenderer(messages, sink, collector);
    renderer.renderReport();
  }

  private InputSource createSource() throws MavenReportException
  {
    final File file = new File(aliasLocation);
    try
    {
      final InputStream in = new BufferedInputStream(new FileInputStream(file));
      final InputSource source = new InputSource();
      source.setSystemId(aliasLocation);
      source.setByteStream(in);
      return source;
    }
    catch (final FileNotFoundException e)
    {
      throw new MavenReportException("Cannot read alias XML file '"
                                     + aliasLocation + "'.", e);
    }
  }

  private AliasesProcessor createProcessor(final InputSource source)
    throws MavenReportException
  {
    try
    {
      return new AliasesProcessor(source);
    }
    catch (final Exception e)
    {
      throw new MavenReportException("Cannot read alias XML from '"
                                     + source.getSystemId() + "'.", e);
    }
  }

  // --- object basics --------------------------------------------------------

}
