package com.collibra.backend.challenge.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.google.common.base.Preconditions.checkArgument;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StringUtils {

    /**
     * Method splits input by any whitespace and return phrase from the indicated position
     *
     * @param text      input text
     * @param position  positive number
     * @return String   text without whitespaces
     */
    public static String phraseAtPosition(final String text, final int position) {
        checkArgument(position > 0, "%s is incorrect value. Position must be positive", position);
        final String[] splittedMessage = text.split("\\s+");
        checkArgument(position <= splittedMessage.length, "Incorrect position. Max value for passed input: %s", splittedMessage.length);
        return splittedMessage[position - 1];
    }
}
