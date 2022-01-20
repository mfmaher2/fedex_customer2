# Original file from Johnson Lu for reference

import datetime, random, string, re, cassandra, sys, socket, time
from pprint import pprint
from cassandra.cluster import Cluster, ExecutionProfile, EXEC_PROFILE_DEFAULT
from cassandra.query import tuple_factory, named_tuple_factory

"""
    python3 seqNum.py 3 300 10.101.33.120 # 3 = block size, 300 = repetition count, 10.101.33.120 = contact host
"""

"""  Seq Nbr testing Table Schema: 
    CREATE TABLE exp_ks.seqnbr_tbl (
        domain text,
        sequencename text,
        currentnbr int,
        endnbr int,
        startnbr int,
        wrapped boolean,
        PRIMARY KEY ((domain, sequencename)))
"""

def lwtTxn(ct, currentNbr):
    print ("trying at: " + str(ct))
    if ct >= retryLmt:      # test how many test to try
        return False
    if (currentNbr + blockSize) < endNbr :
        try:
            result = aSess.execute(lwtUpdateCurrNbrStmt,[(currentNbr + blockSize), domain, sequenceName, currentNbr])
            if (result.one()[0]):       # should be either "True" or "False"
                print ("done")
                for x in range (currentNbr, currentNbr + blockSize):
                    print (str(x) + " :: "+ str(blockSize) + " h:" +hostNm)
                return True
            else:
                print ("failed lwt at:" + str(currentNbr) + " got: " + str(result.one()[1]))
                ct += 1
                return (lwtTxn (ct, result.one()[1]))  # Simple retry; the second element in the resulSet row is the value that disagreed with the "if..." clause.
        except Exception as e:      # it is possible to get a hostNotFound or Quorum error, so should try to avoid those head-achs, need better handler routines here.
            print (e)
            return False


tableName = 'exp_ks.seqnbr_tbl'
domain="customer"; sequenceName = "TNT_ACCT_US"
startNbr = endNbr = currentSeq = 0
hostNm = socket.gethostname()
if (len(sys.argv) != 4):
    blockSize = 3
    repCt = 10
    host = '10.101.32.224'
else:
    blockSize = int(sys.argv[1])    # how many numbers to allocate; block size
    repCt = int(sys.argv[2])        # how many times to repeat the allocation process
    host = sys.argv[3]              # the contact host to use.

retryLmt = 3                        # if lwt fails on the if clause, how many times to retry.

profile = ExecutionProfile(
    consistency_level=cassandra.ConsistencyLevel.LOCAL_QUORUM,
    serial_consistency_level=cassandra.ConsistencyLevel.LOCAL_SERIAL,
    request_timeout=15,
)

cluster = Cluster(contact_points=[host],execution_profiles={EXEC_PROFILE_DEFAULT:profile})
aSess = cluster.connect()
getCurrNbrStmt  = aSess.prepare("select currentNbr, startnbr, endnbr from " + tableName + " where domain=? and sequencename=?")
lwtUpdateCurrNbrStmt = aSess.prepare("update exp_ks.seqnbr_tbl set currentnbr = ? where domain = ? and sequencename = ? if currentnbr = ?")
for x in range(1,repCt):
    rows = aSess.execute(getCurrNbrStmt, [domain, sequenceName])
    aRow = rows.one()
    currentSeq = aRow[0]; startNbr = aRow[1]; endNbr = aRow[2]
    print ("looking at: " + str(currentSeq) + " expecting next seq:" + str(currentSeq + blockSize))
    print (lwtTxn(0, currentSeq))
    time.sleep(random.randint(50,500)/1000)     # simple random sleep between 5 and 500 milli-seconds to simulate random access to the table.
