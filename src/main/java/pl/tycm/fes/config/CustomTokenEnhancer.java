package pl.tycm.fes.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.ldap.userdetails.InetOrgPerson;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;


public class CustomTokenEnhancer implements TokenEnhancer {

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {

		Map<String, Object> additionalInfo = new HashMap<>();
		
		InetOrgPerson person = (InetOrgPerson) authentication.getPrincipal();
		String givenName = person.getGivenName();
		String surName = person.getSn();
		additionalInfo.put("displayName", givenName + " " + surName);
		((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
		return accessToken;
	}
}
