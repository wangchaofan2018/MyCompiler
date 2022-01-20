public interface TokenReader {
    /**
     * 返回当前token 从流中取出 如果流已经为空 返回null
     * */
    public Token read();

    /**
     * 取得当前token 不取出
     * @return
     */
    public Token peek();

    /**
     * Token退回一步
     */
    public void unread();

    /**
     * 获取当前token的读取位置
     * @return
     */
    public int getPosition();

    /**
     * 设置当前token的位置
     * @param position
     */
    public void setPosition(int position);
}
