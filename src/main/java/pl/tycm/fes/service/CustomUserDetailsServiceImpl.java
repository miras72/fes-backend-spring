package pl.tycm.fes.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import org.springframework.security.ldap.userdetails.InetOrgPerson;

public class CustomUserDetailsServiceImpl implements UserDetailsContextMapper {

	@Value("${ad.group.admin}")
	private String AD_GROUP_ADMIN;

	@Value("${ad.group.operator}")
	private String AD_GROUP_OPERATOR;

	@Override
	public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
			Collection<? extends GrantedAuthority> authorities) {

		List<GrantedAuthority> mappedAuthorities = new ArrayList<GrantedAuthority>();
		String[] adGroupAdmins = AD_GROUP_ADMIN.split(",");
		String[] adGroupOperators = AD_GROUP_OPERATOR.split(",");
		boolean isAdminExisted = false;
		boolean isOperatorExisted = false;

		for (GrantedAuthority granted : authorities) {
			for (String groupAdmin : adGroupAdmins) {
				if (granted.getAuthority().equalsIgnoreCase(groupAdmin) && !isAdminExisted) {
					mappedAuthorities.add(new GrantedAuthority() {

						private static final long serialVersionUID = 2076972147732929192L;

						@Override
						public String getAuthority() {
							return "ROLE_ADMIN";
						}

					});
					isAdminExisted = true;
					break;
				}
			}
			for (String groupOperator : adGroupOperators) {
				if (granted.getAuthority().equalsIgnoreCase(groupOperator) && !isOperatorExisted) {
					mappedAuthorities.add(new GrantedAuthority() {

						private static final long serialVersionUID = -5987289264768726108L;

						@Override
						public String getAuthority() {
							return "ROLE_OPERATOR";
						}

					});
					isOperatorExisted = true;
					break;
				}
			}
		}
		if (mappedAuthorities.isEmpty()) {
			mappedAuthorities.add(new GrantedAuthority() {

				private static final long serialVersionUID = -1475841076866182010L;

				@Override
				public String getAuthority() {
					return "";
				}

			});
		}

		InetOrgPerson.Essence p = new InetOrgPerson.Essence(ctx);
		p.setUsername(username);
		p.setAuthorities(mappedAuthorities);
		return p.createUserDetails();
	}

	@Override
	public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
		// TODO Auto-generated method stub

	}

}
