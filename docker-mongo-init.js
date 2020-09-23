// This stays here while we test out this mongo docker container init

db = db.getSiblingDB('profiler')
db.profiles.insert(
    {
	"tstikr": {
	    "2009": {},
	    "2010": {}
	}
    }
)

