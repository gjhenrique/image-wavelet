package br.uel.mdd.db.jdbc;

import java.sql.Connection;

/**
 * @author ${user}
 * @TODO Auto-generated comment
 * <p/>
 * Created by pedro on 28/05/14.
 */
public interface ConnectionFactory {

    void readProperties();
    public abstract Connection getConnection();

}
