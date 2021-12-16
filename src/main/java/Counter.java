import com.sun.jndi.ldap.LdapCtx;
import com.sun.jndi.ldap.LdapResult;
import com.sun.jndi.rmi.registry.RegistryContext;
import com.sun.jndi.rmi.registry.RemoteReference;

import javax.naming.*;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.ldap.LdapName;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Vector;

public class Conter {
    public String name;
    public String host;
    public int port;

    public static void main(String[] args) throws Exception {
//        System.setProperty("com.sun.jndi.rmi.object.trustURLCodebase", "false");
//        System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", "false");
        Config.applyCmdArgs(args);
        String ldapUri = Config.uri;
        CodeBaseURI codeBaseURI = new CodeBaseURI(ldapUri);
        codeBaseURI.parseURI();
        Conter conter = new Conter();
        conter.name = codeBaseURI.name;
        conter.host = codeBaseURI.host;
        conter.port = codeBaseURI.port;
        if (codeBaseURI.schema.equals("ldap")) {
            conter.ldapParse();
        }
        if (codeBaseURI.schema.equals("rmi")) {
            conter.rmiParse();
        }

    }

    public void ldapParse() throws ClassNotFoundException, NamingException, InvocationTargetException, IllegalAccessException {
        Name ldapName = new LdapName("cn=" + this.name);
        Class clazz = Class.forName("com.sun.jndi.ldap.LdapCtx");
        LdapCtx ldapCtx = new LdapCtx("", this.host, this.port, null, false);
        Method m[] = clazz.getDeclaredMethods();
        Method method = null;
        for (int i = 0; i < m.length; i++) {
            if (m[i].getName().equals("doSearchOnce")) {
                method = m[i];
            }
        }
        method.setAccessible(true);
        LdapResult ldapResult = (LdapResult) method.invoke(ldapCtx, ldapName, "(objectClass=*)", new SearchControls(), true);
        Class ldapResultClass = Class.forName("com.sun.jndi.ldap.LdapResult");
        Field f[] = ldapResultClass.getDeclaredFields();
        Field entriesField = null;
        Class ldapEntryClass = Class.forName("com.sun.jndi.ldap.LdapEntry");
        Field f2[] = ldapEntryClass.getDeclaredFields();
        Field att = null;
        for (int k = 0; k < f2.length; k++) {
            if (f2[k].getName().equals("attributes")) {
                att = f2[k];
                att.setAccessible(true);
            }
        }
        for (int j = 0; j < f.length; j++) {
            if (f[j].getName().equals("entries")) {
                entriesField = f[j];
                entriesField.setAccessible(true);
            }
        }
        Vector<Object> ldapEntries = (Vector<Object>) entriesField.get(ldapResult);
        ldapEntries.elementAt(0);
        Attributes attributes = (Attributes) att.get(ldapEntries.elementAt(0));
        Attribute codeBase = attributes.get("javacodebase");
        Attribute factory = attributes.get("javafactory");
        System.out.println((String) codeBase.get(0) + (String) factory.get(0) + ".class");
    }

    public void rmiParse() throws NamingException, RemoteException, ClassNotFoundException, IllegalAccessException, NotBoundException {
        RegistryContext registryContext = new RegistryContext(this.host, this.port, null);
        CompositeName compositeName = new CompositeName();
        compositeName.add(this.name);
        Class registryContextClass = Class.forName("com.sun.jndi.rmi.registry.RegistryContext");
        Field field[] = registryContextClass.getDeclaredFields();
        Field registryField = null;
        for (int i = 0; i < field.length; i++) {
            if (field[i].getName().equals("registry")) {
                registryField = field[i];
                registryField.setAccessible(true);
            }
        }
        Registry registry = (Registry) registryField.get(registryContext);
        RemoteReference remoteReference = (RemoteReference) registry.lookup(compositeName.get(0));
        String rawUri = remoteReference.getReference().getFactoryClassLocation();
        rawUri = rawUri.replace("#", "");
        System.out.println(rawUri + ".class");
        System.exit(1);
    }
}
