package eu.domibus.api.exceptions;

/**
 * Enum for Domibus core errors.
 *
 * @author Federico Martini
 * @since 3.3
 * <p>
 * //TODO change package when refactoring of MSH will take place.
 * //TODO add enum's descriptions.
 */
public enum DomibusCoreError {

    /**
     * Generical error
     */
    DOM_001(001),
    /**
     *
     */
    DOM_002(002),
    /**
     *
     */
    DOM_003(003);

    private final int errorCode;

    DomibusCoreError(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}