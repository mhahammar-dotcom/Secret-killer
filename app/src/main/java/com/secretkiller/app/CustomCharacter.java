package com.secretkiller.app;

public final class CustomCharacter {
    public final String name, role, secret, knowledge, statement;
    public final boolean guilty;
    public CustomCharacter(String name, String role, String secret, String knowledge, String statement, boolean guilty) {
        this.name=name; this.role=role; this.secret=secret; this.knowledge=knowledge; this.statement=statement; this.guilty=guilty;
    }
}
