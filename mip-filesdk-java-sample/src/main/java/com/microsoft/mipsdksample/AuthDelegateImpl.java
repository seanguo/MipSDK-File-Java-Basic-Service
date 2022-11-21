/*
*
* Copyright (c) Microsoft Corporation.
* All rights reserved.
*
* This code is licensed under the MIT License.
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files(the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions :
*
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
*
*/
package com.microsoft.mipsdksample;

import com.microsoft.aad.msal4j.ClientCredentialFactory;
import com.microsoft.aad.msal4j.ClientCredentialParameters;
import com.microsoft.aad.msal4j.ConfidentialClientApplication;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.informationprotection.ApplicationInfo;
import com.microsoft.informationprotection.IAuthDelegate;
import com.microsoft.informationprotection.Identity;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class AuthDelegateImpl implements IAuthDelegate {

    public static final String MIP_CLIENT_SECRET_VALUE = "mipClientSecretValue";
    private static String CLIENT_ID = "";
    private static String SECRET_VALUE = "";
    private static String AUTHORITY = "";
    private static Set<String> SCOPE = Collections.singleton("");

    public AuthDelegateImpl(ApplicationInfo appInfo)
    {
        CLIENT_ID = appInfo.getApplicationId();
        SECRET_VALUE = System.getProperty(MIP_CLIENT_SECRET_VALUE) != null ? System.getProperty(MIP_CLIENT_SECRET_VALUE) : System.getenv(MIP_CLIENT_SECRET_VALUE);
    }

    @Override
    public String acquireToken(Identity userName, String authority, String resource, String claims) {
        if(resource.endsWith("/")){
            SCOPE = Collections.singleton(resource + ".default");        
        }
        else {
            SCOPE = Collections.singleton(resource + "/.default");        
        }

        System.out.println("AUTHORITY: " + AUTHORITY);
        AUTHORITY = "https://login.microsoftonline.com/f2a8ad30-c8dc-422f-b038-ade361364001";
        String token = "";
        try {
            token = acquireTokenInteractive().accessToken();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return token;
    }

    private static IAuthenticationResult acquireTokenInteractive() throws Exception {

        ConfidentialClientApplication app = ConfidentialClientApplication.builder(
                        CLIENT_ID,
                        ClientCredentialFactory.createFromSecret(SECRET_VALUE))
                .authority(AUTHORITY)
                .build();
        // With client credentials flows the scope is ALWAYS of the shape "resource/.default", as the
        // application permissions need to be set statically (in the portal), and then granted by a tenant administrator
        ClientCredentialParameters clientCredentialParam = ClientCredentialParameters.builder(
                        SCOPE)
                .build();

        CompletableFuture<IAuthenticationResult> future = app.acquireToken(clientCredentialParam);
        return future.get();
    }
}
