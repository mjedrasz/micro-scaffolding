import com.scaffold.commons.test.category.*

runner {
	if (System.properties['test.exclude.fast']) {
		exclude {
			annotation Fast
		}
	}
	if (System.properties['test.exclude.slow']) {
		exclude {
			annotation Slow
		}
	}
	if (System.properties['test.exclude.db']) {
		exclude {
			annotation Database
		}
	}
	def x = []
	if (System.properties['test.include.fast']) {
		x << Fast
	}
//		include {
//			annotations << Fast
//		}
//	}
	if (System.properties['test.include.slow']) {
		include {
			annotation Slow
		}
	}
	if (System.properties['test.include.db']) {
		x << Database
	}
//		include {
//			annotations << Database
//		}
//	}
	include {
		annotations x
	}
	filterStackTrace true // this is the default
}

