package protocolsupport.api;

// PS isn't published, use this to access its API
public abstract class Connection {

    public abstract static class PacketListener {

        public void onPacketReceiving(final PacketEvent event) {
            throw new UnsupportedOperationException();
        }

        public static class PacketEvent {

            public Object getPacket() {
                throw new UnsupportedOperationException();
            }
        }
    }
}
