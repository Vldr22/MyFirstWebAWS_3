package org.education.firstwebproject.exception.validation;

import org.education.firstwebproject.exception.messages.Messages;

public class FileEmptyException extends RuntimeException {
  public FileEmptyException() {
    super(Messages.FILE_EMPTY);
  }
}
