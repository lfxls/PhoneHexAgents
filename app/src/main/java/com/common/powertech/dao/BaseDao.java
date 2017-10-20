package com.common.powertech.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.support.DatabaseConnection;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

/**
 * 数据库操作DAO基类
 * 
 * @author luyq
 * @version V1.0
 * @date 2015/10/26
 */
public class BaseDao<T, ID> {
	public Dao<T, ID> dao = null;
	private DataType[] types = null;
	private OrmLiteSqliteOpenHelper helper = null;

	@SuppressWarnings("unchecked")
	public BaseDao(Context context, @SuppressWarnings("rawtypes") Class clazz) {

		helper = OpenHelperManager.getHelper(context, DataHelper.class);

		try {
			this.dao = helper.getDao(clazz);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * *************************Query
	 * operation***********************************
	 */
	public boolean isExists(ID id) {
		try {
			return this.dao.idExists(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public T findById(ID id) {
		try {
			return this.dao.queryForId(id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<T> list() {
		try {
			return this.dao.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<T> findAllByField(String fieldName, Object value) {
		try {
			return this.dao.queryForEq(fieldName, value);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<T> findBySQL(String sql) {
		GenericRawResults<Object[]> results = null;
		try {
			results = this.dao.queryRaw(sql, types);
			return (List<T>) results.getResults();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public long queryRawValueBySQL(String sql) {
		long results = 0;
		try {
			results = this.dao.queryRawValue(sql);
			return results;
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	
	public List<String[]> queryRawBySQL(String sql) {
		GenericRawResults<String[]> results = null;
		try {
			results = this.dao.queryRaw(sql);
			return (List<String[]>) results.getResults();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}



	/**
	 * *************************Add
	 * operation*************************************
	 */
	public boolean create(T object) {
		try {
			return (this.dao.create(object) == 1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean createIfNotExist(T object) {
		try {
			return (this.dao.createIfNotExists(object).equals(object));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean createOrUpdate(T object) {
		Dao.CreateOrUpdateStatus result = null;
		try {
			result = this.dao.createOrUpdate(object);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (result.isCreated() || result.isUpdated()) {
			if (result.getNumLinesChanged() > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * **********************Update
	 * operation************************************
	 */
	public boolean update(T object) {
		if (object == null) {
			return false;
		}
		try {
			return (this.dao.update(object) == 1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean updateId(T object, ID id) {
		if (object == null) {
			return false;
		}
		try {
			return (this.dao.updateId(object, id) == 1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean refresh(T object) {
		if (object == null) {
			return false;
		}
		try {
			return (this.dao.refresh(object) == 1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * ************************Delete
	 * operation***********************************
	 */
	public boolean delete(T object) {
		if (object == null) {
			return false;
		}
		try {
			return (this.dao.delete(object) == 1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean deleteList(Collection<T> objects) {
		if (objects == null || objects.size() == 0) {
			return false;
		}
		try {
			return (this.dao.delete(objects) == objects.size());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean deleteById(ID id) {
		try {
			return (this.dao.deleteById(id) == 1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean deleteByIds(Collection<ID> ids) {
		if (ids == null) {
			return false;
		}
		try {
			return (this.dao.deleteIds(ids) == ids.size());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * ************************Excute
	 * operation***********************************
	 */
	public boolean excute(String sql) {
		try {
			this.dao.executeRawNoArgs(sql);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
/**
 * 设置是否自动提交（事务时用到）
 * 
 * @param isAutoCommit
 * @return
 */
	public boolean setAutoCommit(boolean isAutoCommit) {
		try {
			this.dao.setAutoCommit(isAutoCommit);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * 回滚事务
	 * 
	 * @param sql
	 * @return
	 */
		public void rollback() {
			try {
				this.dao.rollBack(helper.getConnectionSource().getReadWriteConnection());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		/**
		 * 提交事务
		 * 
		 * @param sql
		 * @return
		 */
			public void commit() {
				try {
					this.dao.commit(helper.getConnectionSource().getReadWriteConnection());
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
	
}
