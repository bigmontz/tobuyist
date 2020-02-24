package bigmontz.tobuyist.business.usecase.adapter

import java.util.concurrent.CompletionStage

import java.util.function.Function

interface Adapter<In, Out> : Function<In, CompletionStage<Out>>