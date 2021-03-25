package exceptions;

public class BadParamsException extends Exception {

    // TODO: add attribute for
    /* L’un des paramètres mentionné n’existe pas pour l’algorithme choisi.
    — La valeur du jeu de paramètres est invalide */
    public BadParamsException(String message) {
        super(message);
    }
}
