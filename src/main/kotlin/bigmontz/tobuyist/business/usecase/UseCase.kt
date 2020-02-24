package bigmontz.tobuyist.business.usecase

import java.util.concurrent.CompletionStage
import java.util.function.Function

/**
 * Defines the format of the use case on the program
 *
 */
interface UseCase<In, Out> : Function<In, CompletionStage<Out>>