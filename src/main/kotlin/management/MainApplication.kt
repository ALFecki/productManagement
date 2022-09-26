package management

import io.micronaut.runtime.Micronaut.*


	fun main(args: Array<String>) {

		build()
			.args(*args)
			.packages("management")
			.start()

	}


