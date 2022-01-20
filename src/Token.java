public interface Token {

    //Token即词 通过type定位类型 text获取值

    public TokenType getType();

    public String getText();
}
