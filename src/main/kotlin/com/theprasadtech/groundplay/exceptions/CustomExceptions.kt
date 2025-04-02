package com.theprasadtech.groundplay.exceptions

import org.springframework.http.HttpStatus

abstract class BaseException(
    val errorCode: String,
    override val message: String,
    val httpStatus: HttpStatus,
) : RuntimeException(message)

class ResourceNotFoundException(
    resourceType: String,
    resourceId: Any,
) : BaseException(
        errorCode = "RESOURCE_NOT_FOUND",
        message = "$resourceType with ID $resourceId not found",
        httpStatus = HttpStatus.NOT_FOUND,
    )

class ResourceAlreadyExistsException(
    resourceType: String,
    field: String,
    value: Any,
) : BaseException(
        errorCode = "RESOURCE_ALREADY_EXISTS",
        message = "$resourceType with $field '$value' already exists",
        httpStatus = HttpStatus.CONFLICT,
    )

class PlayerNotAvailableException(
    playerIds: List<Long>,
) : BaseException(
        errorCode = "PLAYER_NOT_AVAILABLE",
        message = "Players with IDs ${playerIds.joinToString(", ")} are not available at the requested time",
        httpStatus = HttpStatus.CONFLICT,
    )

class GameConflictException(
    message: String,
) : BaseException(
        errorCode = "GAME_CONFLICT",
        message = message,
        httpStatus = HttpStatus.CONFLICT,
    )

class ValidationException(
    message: String,
) : BaseException(
        errorCode = "VALIDATION_ERROR",
        message = message,
        httpStatus = HttpStatus.BAD_REQUEST,
    )

class UnauthorizedOperationException(
    message: String,
) : BaseException(
        errorCode = "UNAUTHORIZED_OPERATION",
        message = message,
        httpStatus = HttpStatus.FORBIDDEN,
    )

class GameFullException(
    gameId: Long,
) : BaseException(
        errorCode = "GAME_FULL",
        message = "Game with ID $gameId is already full",
        httpStatus = HttpStatus.CONFLICT,
    )
