package network;

public enum Address {
    LOGIN("login"),
    CREATEUSER("createUser"),
    SENDMSG("sendMsg"),
    ETCHMSG("fetchMessages");

    private String address;

    Address(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return this.address;
    }
}
