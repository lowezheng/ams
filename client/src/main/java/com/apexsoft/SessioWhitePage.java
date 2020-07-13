package com.apexsoft;

import com.apexsoft.aas.common.session.SessionApiWhitelist;
import org.springframework.stereotype.Component;

@Component
public class SessioWhitePage extends SessionApiWhitelist {
    @Override
    protected void buildPage() {
        pageList.add("*");
    }

    @Override
    protected void buildFunc() {
        super.buildFunc();
    }
}
