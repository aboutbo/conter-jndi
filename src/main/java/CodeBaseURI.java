import javax.naming.Name;

public class CodeBaseURI {
    public String schema;
    public String host;
    public int port;
    public String name;
    public String uri;
    public CodeBaseURI(String uri){
        this.uri=uri;
    }
    public void parseURI() throws Exception {
        int colonIndex=this.uri.indexOf(":");
        if (colonIndex<0){
            throw new Exception("Invalid URI!");
        } else {
            this.schema=this.uri.substring(0,colonIndex);
            String left=this.uri.substring(colonIndex+3);
            colonIndex=left.indexOf(":");
            int slashIndex=left.indexOf("/");
            if (slashIndex<0){
                throw new Exception("Invalid URI");
            } else {
                this.name=left.substring(slashIndex+1);
                if (colonIndex<0){
                    this.port=80;
                    this.host=left.substring(0,slashIndex);
                }else{
                    this.port=Integer.parseInt(left.substring(colonIndex+1,slashIndex));
                    this.host=left.substring(0,colonIndex);
                }
            }


        }

    }
}
