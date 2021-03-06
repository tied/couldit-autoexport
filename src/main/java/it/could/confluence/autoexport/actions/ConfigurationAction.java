/* ========================================================================== *
 *   Copyright (c) 2006, Pier Paolo Fumagalli <mailto:pier@betaversion.org>   *
 *                            All rights reserved.                            *
 * ========================================================================== *
 *                                                                            * 
 * Redistribution and use in source and binary forms, with or without modifi- *
 * cation, are permitted provided that the following conditions are met:      *
 *                                                                            * 
 *  - Redistributions of source code must retain the  above copyright notice, *
 *    this list of conditions and the following disclaimer.                   *
 *                                                                            * 
 *  - Redistributions  in binary  form  must  reproduce the  above  copyright *
 *    notice,  this list of conditions  and the following  disclaimer  in the *
 *    documentation and/or other materials provided with the distribution.    *
 *                                                                            * 
 *  - Neither the name of Pier Fumagalli, nor the names of other contributors *
 *    may be used to endorse  or promote products derived  from this software *
 *    without specific prior written permission.                              *
 *                                                                            * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDER AND CONTRIBUTORS "AS IS" *
 * AND ANY EXPRESS OR IMPLIED WARRANTIES,  INCLUDING, BUT NOT LIMITED TO, THE *
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE *
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER  OR CONTRIBUTORS BE *
 * LIABLE  FOR ANY  DIRECT,  INDIRECT,  INCIDENTAL,  SPECIAL,  EXEMPLARY,  OR *
 * CONSEQUENTIAL  DAMAGES  (INCLUDING,  BUT  NOT LIMITED  TO,  PROCUREMENT OF *
 * SUBSTITUTE GOODS OR SERVICES;  LOSS OF USE, DATA, OR PROFITS;  OR BUSINESS *
 * INTERRUPTION)  HOWEVER CAUSED AND ON  ANY THEORY OF LIABILITY,  WHETHER IN *
 * CONTRACT,  STRICT LIABILITY,  OR TORT  (INCLUDING NEGLIGENCE OR OTHERWISE) *
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE *
 * POSSIBILITY OF SUCH DAMAGE.                                                *
 * ========================================================================== */
package it.could.confluence.autoexport.actions;

import it.could.confluence.autoexport.ConfigurationManager;
import it.could.confluence.autoexport.TemplatesManager;
import it.could.confluence.localization.LocalizedAction;
import it.could.confluence.localization.LocalizedException;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.atlassian.confluence.core.Administrative;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceType;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <p>An action managing the AutoExport plugin configuration.</p>
 */
public class ConfigurationAction extends LocalizedAction
implements Administrative {

    private SpaceManager spaceManager;
    private ConfigurationManager configurationManager;
    private TemplatesManager templatesManager;

    /** <p>The currently configured encoding.</p> */
    private String encoding = null;
    /** <p>The currently configured root path.</p> */
    private String rootPath = null;
    /** <p>The currently configured user name.</p> */
    private String userName = null;

    private static final Log log = LogFactory.getLog(ConfigurationAction.class);

    /* ====================================================================== */
    /* BEAN SETTER METHODS FOR SPRING AUTO-WIRING                             */
    /* ====================================================================== */


    /* ====================================================================== */
    /* ACTION METHODS                                                         */
    /* ====================================================================== */

    /**
     * <p>Review the current configuration.</p>
     */
    public String execute() {
        this.encoding = this.configurationManager.getEncoding();
        this.rootPath = this.configurationManager.getRootPath();
        this.userName = this.configurationManager.getUserName();
        try {
            if (this.configurationManager.isConfigured()) return SUCCESS;
            this.addActionError(this.getText("err.unconfigured"));
        } catch (LocalizedException exception) {
            this.log.warn(this.getText("err.misconfigured"), exception);
            this.addActionError(this.getText("err.misconfigured"));
        }
        return SUCCESS;
    }

    /**
     * <p>Save the current configuration.</p>
     */
    public String configure() {
        try {
            /* Validate the current values for sanity */
            this.configurationManager.validateEncoding(this.encoding);
            this.configurationManager.validateRootPath(this.rootPath);
            this.configurationManager.validateUserName(this.userName);

            /* Store the current values in the configuration */
            this.configurationManager.setEncoding(this.encoding);
            this.configurationManager.setRootPath(this.rootPath);
            this.configurationManager.setUserName(this.userName);

            /* Save the configuration for when confluence restarts */
            this.configurationManager.save();
            this.addActionMessage(this.getText("msg.saved"));

        } catch (LocalizedException exception) {
            this.log.warn(this.getText("err.misconfigured"), exception);
            this.addActionError(this.getText("err.misconfigured"));
        }
        return SUCCESS;
    }

    /**
     * <p>Delete the current configuration.</p>
     */
    public String unconfigure() {
        try {
            this.configurationManager.delete();
            this.addActionMessage(this.getText("msg.deleted"));
        } catch (LocalizedException exception) {
            this.log.warn(this.getText("err.misconfigured"), exception);
            this.addActionError(this.getText("err.misconfigured"));
        }
        return SUCCESS;
    }

    /* ====================================================================== */
    /* SETTERS AND GETTERS FOR PARAMETER VALUES                               */
    /* ====================================================================== */

    /**
     * <p>Parameter value getter.</p>
     */
    public String getEncoding() {
        try {
            return this.configurationManager.validateEncoding(this.encoding);
        } catch (LocalizedException exception) {
            this.addFieldError("encoding", exception.getMessage());
        }
        return this.encoding;
    }

    /**
     * <p>Parameter value setter.</p>
     */
    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    /**
     * <p>Parameter value getter.</p>
     */
    public String getRootPath() {
        try {
            return this.configurationManager.validateRootPath(this.rootPath);
        } catch (LocalizedException exception) {
            this.addFieldError("rootPath", exception.getMessage());
        }
        return this.rootPath;
    }

    /**
     * <p>Parameter value setter.</p>
     */
    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    /**
     * <p>Parameter value getter.</p>
     */
    public String getUserName() {
        try {
            return this.configurationManager.validateUserName(this.userName);
        } catch (LocalizedException exception) {
            this.addFieldError("userName", exception.getMessage());
        }
        return this.userName;
    }

    /**
     * <p>Parameter value setter.</p>
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /* ====================================================================== */
    /* OTHER TEMPLATE METHODS                                                 */
    /* ====================================================================== */

    /**
     * <p>Check if the specified space has a custom template.</p>
     */
    public boolean hasCustomTemplate(String space) {
        return this.templatesManager.hasCustomTemplate(space);
    }
    
    /**
     * <p>Return a {@link List} of all spaces in Confluence.</p>
     */
    public List getSpaces() {
    	final List list = new ArrayList();
    	list.addAll(this.spaceManager.getSpacesByType(SpaceType.GLOBAL));
    	list.addAll(this.spaceManager.getSpacesByType(SpaceType.PERSONAL));
    	return list;
    }

    /**
     * <p>Return a {@link List} of all supported encodings.</p>
     */
    public List getEncodings() {
        return new ArrayList(Charset.availableCharsets().keySet());
    }

    public void setSpaceManager(SpaceManager spaceManager)
    {
        this.spaceManager = spaceManager;
    }

    public void setConfigurationManager(ConfigurationManager configurationManager)
    {
        this.configurationManager = configurationManager;
    }

    public void setTemplatesManager(TemplatesManager templatesManager)
    {
        this.templatesManager = templatesManager;
    }
}