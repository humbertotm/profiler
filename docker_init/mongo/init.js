// This stays here while we test out this mongo docker container init
// Was not executed during compose initialization. Need to determine if this is needed
// at all or if first inserts and db selection from lein app are enough to initialize db
// and collection.
db = db.getSiblingDB('profiler')
db.profiles.insert(
    {
	"tstikr": {
	    "2009": {},
	    "2010": {}
	}
    }
)

