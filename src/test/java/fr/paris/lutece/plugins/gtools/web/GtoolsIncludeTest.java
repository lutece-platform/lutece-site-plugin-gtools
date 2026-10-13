/*
 * Copyright (c) 2002-2026, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the description of 'Mairie de Paris' nor 'Lutece' nor the descriptions of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.gtools.web;

import fr.paris.lutece.plugins.gtools.service.GoogleSitePropertiesGroup;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.site.properties.ILocalizedSitePropertiesGroup;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests of the gtools page includes and site properties group
 */
public class GtoolsIncludeTest extends LuteceTestCase
{
    private static final String KEY_ANALYTICS_CODE = "gtools.site_property.analytics.code";
    private static final String KEY_WEBMASTER_TOOLS_CODE = "gtools.site_property.webmaster_tools.code";
    private static final String ANALYTICS_CODE = "UA-TEST-4242";
    private static final String WEBMASTER_TOOLS_CODE = "verif-test-4242";

    @Inject
    private Instance<ILocalizedSitePropertiesGroup> _groups;

    private String _strAnalyticsBackup;
    private String _strWebmasterBackup;

    /**
     * Restores the datastore values changed by a test.
     */
    @AfterEach
    public void restoreDatastore( )
    {
        restore( KEY_ANALYTICS_CODE, _strAnalyticsBackup );
        restore( KEY_WEBMASTER_TOOLS_CODE, _strWebmasterBackup );
    }

    /**
     * The analytics include renders the configured code.
     */
    @Test
    public void testAnalyticsInclude( )
    {
        _strAnalyticsBackup = DatastoreService.getDataValue( KEY_ANALYTICS_CODE, null );
        DatastoreService.setDataValue( KEY_ANALYTICS_CODE, ANALYTICS_CODE );

        Map<String, Object> rootModel = new HashMap<>( );
        new AnalyticsInclude( ).fillTemplate( rootModel, null, 0, new MockHttpServletRequest( ) );

        String strHtml = (String) rootModel.get( "google_analytics" );
        assertNotNull( strHtml );
        assertTrue( strHtml.contains( ANALYTICS_CODE ), strHtml );
    }

    /**
     * The site verification include renders the configured code in a meta tag.
     */
    @Test
    public void testSiteVerificationInclude( )
    {
        _strWebmasterBackup = DatastoreService.getDataValue( KEY_WEBMASTER_TOOLS_CODE, null );
        DatastoreService.setDataValue( KEY_WEBMASTER_TOOLS_CODE, WEBMASTER_TOOLS_CODE );

        Map<String, Object> rootModel = new HashMap<>( );
        new SiteVerificationInclude( ).fillTemplate( rootModel, null, 0, new MockHttpServletRequest( ) );

        String strHtml = (String) rootModel.get( "google_meta_site_verification" );
        assertNotNull( strHtml );
        assertTrue( strHtml.contains( "<meta name=\"google-site-verification\" content=\"" + WEBMASTER_TOOLS_CODE + "\"" ), strHtml );
    }

    /**
     * Without a configured code the includes render no tag.
     */
    @Test
    public void testIncludesWithoutCode( )
    {
        _strAnalyticsBackup = DatastoreService.getDataValue( KEY_ANALYTICS_CODE, null );
        _strWebmasterBackup = DatastoreService.getDataValue( KEY_WEBMASTER_TOOLS_CODE, null );
        DatastoreService.removeData( KEY_ANALYTICS_CODE );
        DatastoreService.removeData( KEY_WEBMASTER_TOOLS_CODE );

        Map<String, Object> rootModel = new HashMap<>( );
        new AnalyticsInclude( ).fillTemplate( rootModel, null, 0, new MockHttpServletRequest( ) );
        new SiteVerificationInclude( ).fillTemplate( rootModel, null, 0, new MockHttpServletRequest( ) );

        String strAnalytics = (String) rootModel.get( "google_analytics" );
        String strVerification = (String) rootModel.get( "google_meta_site_verification" );
        assertFalse( strAnalytics.contains( "_setAccount" ), strAnalytics );
        assertFalse( strVerification.contains( "<meta" ), strVerification );
    }

    /**
     * Without a request the includes render nothing.
     */
    @Test
    public void testIncludesWithoutRequest( )
    {
        Map<String, Object> rootModel = new HashMap<>( );
        new AnalyticsInclude( ).fillTemplate( rootModel, null, 0, null );
        new SiteVerificationInclude( ).fillTemplate( rootModel, null, 0, null );

        assertEquals( "", rootModel.get( "google_analytics" ) );
        assertEquals( "", rootModel.get( "google_meta_site_verification" ) );
    }

    /**
     * The Google site properties group is a CDI bean seen by the site properties service.
     */
    @Test
    public void testSitePropertiesGroupIsDiscovered( )
    {
        assertTrue( _groups.stream( ).anyMatch( GoogleSitePropertiesGroup.class::isInstance ) );
    }

    /**
     * Puts a datastore value back to its saved state.
     *
     * @param strKey
     *            the key
     * @param strValue
     *            the saved value, null when the key did not exist
     */
    private static void restore( String strKey, String strValue )
    {
        if ( strValue == null )
        {
            DatastoreService.removeData( strKey );
        }
        else
        {
            DatastoreService.setDataValue( strKey, strValue );
        }
    }
}
