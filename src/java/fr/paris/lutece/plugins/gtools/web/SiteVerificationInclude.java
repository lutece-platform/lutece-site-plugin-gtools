/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import fr.paris.lutece.portal.service.content.PageData;
import fr.paris.lutece.portal.service.datastore.DatastoreService;
import fr.paris.lutece.portal.service.includes.PageInclude;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * Include of the site verification code
 */
public class SiteVerificationInclude implements PageInclude
{
    private static final String KEY_WEBMASTER_TOOLS_CODE = "gtools.site_property.webmaster_tools.code";
    private static final String MARK_WEBMASTER_TOOLS = "google_meta_site_verification";
    private static final String PLUGIN_NAME = "gtools";
    private static final String TEMPLATE_INCLUDE_WEBMASTER_TOOLS_CODE = "skin/plugins/gtools/include_webmaster_tools.html";
    private static final String MARK_INCLUDE_WEBMASTER_TOOLS_CODE = "gvalidation_code";
    private Plugin _plugin;

    /**
     * Substitue specific Freemarker markers in the page template.
     * @param rootModel the HashMap containing markers to substitute
     * @param data A PageData object containing applications data
     * @param nMode The current mode
     * @param request The HTTP request
     */
    @Override
    public void fillTemplate( Map<String, Object> rootModel, PageData data, int nMode, HttpServletRequest request )
    {
        _plugin = PluginService.getPlugin( PLUGIN_NAME );

        if( _plugin != null && request != null )
        {
	        Map<String, Object> model = new HashMap<String, Object>(  );
	        String strCode = DatastoreService.getDataValue( KEY_WEBMASTER_TOOLS_CODE, "<no code provided>" );
	        model.put( MARK_INCLUDE_WEBMASTER_TOOLS_CODE, strCode );
	
	        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_INCLUDE_WEBMASTER_TOOLS_CODE,
	                request.getLocale(  ), model );
	
	        rootModel.put( MARK_WEBMASTER_TOOLS, template.getHtml(  ) );
        }
        else {
	        rootModel.put( MARK_WEBMASTER_TOOLS, "" );
        }
    }
}
