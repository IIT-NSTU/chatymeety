package com.arnab.chatymeety;

public class Chat {
    private String lastMsg;
    private boolean seen;
    private long time;

    public Chat() {
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public Chat(String lastMsg, boolean seen, long time) {
        this.lastMsg=lastMsg;
        this.seen = seen;
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
