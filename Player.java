package com.secretkiller.app;

public class Player {
    public final int id; public final String name; public String role,secret,knowledge,statement; public final boolean guilty; public boolean eliminated=false;
    public Player(int id,String name,String role,boolean guilty,String secret,String knowledge,String statement){this.id=id;this.name=name;this.role=role;this.guilty=guilty;this.secret=secret;this.knowledge=knowledge;this.statement=statement;}
}
