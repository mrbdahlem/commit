package run.mycode.commit.model;

import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;

import java.util.Map;

public class GithubPrincipalExtractor implements PrincipalExtractor {

    @Override
    public Object extractPrincipal(Map<String, Object> map) {
        System.out.println(">>>>>>>>>>>>" + map.get("login"));
        return map.get("login");
    }
}