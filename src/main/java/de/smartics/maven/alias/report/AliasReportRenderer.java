/*
 * Copyright 2012 smartics, Kronseder & Reiner GmbH
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
package de.smartics.maven.alias.report;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.util.StringUtils;

import de.smartics.maven.alias.domain.Alias;
import de.smartics.maven.alias.domain.AliasExtension;
import de.smartics.maven.alias.domain.AliasGroup;
import de.smartics.maven.alias.domain.ExtensionGroup;

/**
 * Renders the alias report.
 */
public final class AliasReportRenderer
{ // NOPMD
  // ********************************* Fields *********************************

  // --- constants ------------------------------------------------------------

  // --- members --------------------------------------------------------------

  /**
   * The sink to write to.
   */
  private final Sink sink;

  /**
   * The resource bundle to access localized messages.
   */
  private final ResourceBundle messages;

  /**
   * The collection of alias groups to report.
   */
  private final ReportAliasCollector collector;

  // ****************************** Initializer *******************************

  // ****************************** Constructors ******************************

  /**
   * Default constructor.
   *
   * @param messages the resource bundle to access localized messages.
   * @param sink the sink to write to.
   * @param collector the collection of alias groups to report.
   */
  public AliasReportRenderer(final ResourceBundle messages, final Sink sink,
      final ReportAliasCollector collector)
  {
    this.sink = sink;
    this.messages = messages;
    this.collector = collector;
  }

  // ****************************** Inner Classes *****************************

  // ********************************* Methods ********************************

  // --- init -----------------------------------------------------------------

  // --- get&set --------------------------------------------------------------

  // --- business -------------------------------------------------------------

  /**
   * Renders the report to the instance's sink.
   *
   * @throws MavenReportException if the report cannot be rendered.
   */
  public void renderReport() throws MavenReportException
  {
    sink.head();
    sink.title();
    sink.text(messages.getString("report.name"));
    sink.title_();
    sink.head_();

    sink.body();
    renderBody();
    sink.body_();
    sink.flush();
    sink.close();
  }

  /**
   * Renders the body of the report.
   *
   * @throws MavenReportException if the report cannot be rendered.
   */
  private void renderBody() throws MavenReportException
  {
    sink.section1();

    sink.sectionTitle1();
    sink.text(messages.getString("report.name"));
    sink.sectionTitle1_();

    sink.paragraph();
    sink.text(messages.getString("report.description"));
    sink.paragraph_();
    sink.paragraph();
    sink.rawText(messages.getString("report.further-information"));
    sink.paragraph_();

    renderAliasGroups();

    renderExtensions();

    renderFooter();
    sink.section1_();
  }

  private void renderAliasGroups()
  {
    for (final AliasGroup group : collector)
    {
      final String sectionName = group.getName();
      sink.sectionTitle2();
      sink.text(sectionName);
      sink.sectionTitle2_();

      final String comment = group.getComment();
      if (comment != null)
      {
        sink.paragraph();
        sink.rawText(comment);
        sink.paragraph_();
      }

      renderTableStart();
      for (final Alias alias : group.getAliases())
      {
        renderTableRow(alias);
      }

      renderTableEnd();
    }
  }

  private void renderTableStart()
  {
    sink.table();
    sink.tableRow();

    sink.tableHeaderCell("100");
    final String nameLabel = getLabel("report.alias.name");
    sink.text(nameLabel);
    sink.tableHeaderCell_();

    sink.tableHeaderCell("300");
    final String commandLabel = getLabel("report.alias.command");
    sink.text(commandLabel);
    sink.tableHeaderCell_();

    sink.tableHeaderCell();
    final String commentLabel = getLabel("report.alias.comment");
    sink.text(commentLabel);
    sink.tableHeaderCell_();

    sink.tableRow_();
  }

  private void renderTableEnd()
  {
    sink.table_();
  }

  private void renderTableRow(final Alias alias)
  {
    sink.tableRow();

    sink.tableCell();
    sink.monospaced();
    sink.text(alias.getName());
    sink.monospaced_();
    sink.tableCell_();

    sink.tableCell();
    sink.monospaced();
    sink.text(alias.getCommand());
    sink.monospaced_();
    sink.tableCell_();

    sink.tableCell();
    final String comment = alias.getComment();
    if (comment != null)
    {
      sink.rawText(comment);
    }
    else
    {
      sink.text("-");
    }
    sink.tableCell_();

    sink.tableCell_();
    sink.tableRow_();
  }

  private String getLabel(final String key)
  {
    try
    {
      return messages.getString(key);
    }
    catch (final MissingResourceException e)
    {
      return key;
    }
  }

  /**
   * Renders the footer text.
   */
  private void renderFooter()
  {
    final String footerText = messages.getString("report.footer");
    if (StringUtils.isNotBlank(footerText))
    {
      sink.rawText(footerText);
    }
  }

  private void renderExtensions()
  {
    for (final ExtensionGroup group : collector.getExtensionGroups())
    {
      final AliasExtension extension = group.getExtension();

      final String sectionName = extension.getName();

      sink.sectionTitle2();
      sink.text("..." + sectionName);
      final String env = extension.getEnv();
      if (StringUtils.isNotBlank(env))
      {
        sink.text(" (" + env + ')');
      }
      sink.sectionTitle2_();

      final String comment = extension.getComment();
      if (comment != null)
      {
        sink.paragraph();
        sink.rawText(comment);
        sink.paragraph_();
      }

      renderTableStart();
      for (final Alias alias : group.getAliases())
      {
        renderTableRow(alias);
      }
      renderTableEnd();
    }
  }

  // --- object basics --------------------------------------------------------

}
