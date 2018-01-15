package org.schabi.newpipe.database.stream.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import org.schabi.newpipe.database.BasicDAO;
import org.schabi.newpipe.database.stream.StreamStatisticsEntry;
import org.schabi.newpipe.database.stream.model.StreamHistoryEntity;

import java.util.List;

import io.reactivex.Flowable;

import static org.schabi.newpipe.database.stream.StreamStatisticsEntry.STREAM_LATEST_DATE;
import static org.schabi.newpipe.database.stream.StreamStatisticsEntry.STREAM_WATCH_COUNT;
import static org.schabi.newpipe.database.stream.model.StreamEntity.STREAM_ID;
import static org.schabi.newpipe.database.stream.model.StreamEntity.STREAM_TABLE;
import static org.schabi.newpipe.database.stream.model.StreamHistoryEntity.JOIN_STREAM_ID;
import static org.schabi.newpipe.database.stream.model.StreamHistoryEntity.STREAM_ACCESS_DATE;
import static org.schabi.newpipe.database.stream.model.StreamHistoryEntity.STREAM_HISTORY_TABLE;

@Dao
public abstract class StreamHistoryDAO implements BasicDAO<StreamHistoryEntity> {
    @Override
    @Query("SELECT * FROM " + STREAM_HISTORY_TABLE)
    public abstract Flowable<List<StreamHistoryEntity>> getAll();

    @Override
    @Query("DELETE FROM " + STREAM_HISTORY_TABLE)
    public abstract int deleteAll();

    @Override
    public Flowable<List<StreamHistoryEntity>> listByService(int serviceId) {
        throw new UnsupportedOperationException();
    }

    @Query("DELETE FROM " + STREAM_HISTORY_TABLE + " WHERE " + JOIN_STREAM_ID + " = :streamId")
    public abstract int deleteHistory(final long streamId);

    @Query("SELECT * FROM " + STREAM_TABLE +

            // Select the latest entry and watch count for each stream id on history table
            " INNER JOIN " +
            "(SELECT " + JOIN_STREAM_ID + ", " +
            "  MAX(" + STREAM_ACCESS_DATE + ") AS " + STREAM_LATEST_DATE + ", " +
            "  COUNT(*) AS " + STREAM_WATCH_COUNT +
            " FROM " + STREAM_HISTORY_TABLE + " GROUP BY " + JOIN_STREAM_ID + ")" +

            " ON " + STREAM_ID + " = " + JOIN_STREAM_ID +
            " ORDER BY " + STREAM_ACCESS_DATE + " DESC")
    public abstract Flowable<List<StreamStatisticsEntry>> getStatistics();
}
