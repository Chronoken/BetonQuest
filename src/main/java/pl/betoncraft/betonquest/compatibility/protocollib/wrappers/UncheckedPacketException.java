package pl.betoncraft.betonquest.compatibility.protocollib.wrappers;

public class UncheckedPacketException extends RuntimeException {

    private static final long serialVersionUID = 60789910395201791L;

    public UncheckedPacketException() {
        super();
    }

    public UncheckedPacketException(final String message) {
        super(message);
    }

    public UncheckedPacketException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UncheckedPacketException(final Throwable cause) {
        super(cause);
    }
}
