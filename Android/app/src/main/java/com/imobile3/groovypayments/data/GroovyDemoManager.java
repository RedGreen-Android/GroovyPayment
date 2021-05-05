package com.imobile3.groovypayments.data;

import android.content.Context;
import android.os.AsyncTask;

import com.imobile3.groovypayments.MainApplication;
import com.imobile3.groovypayments.data.entities.CartEntity;
import com.imobile3.groovypayments.data.entities.CartProductEntity;
import com.imobile3.groovypayments.data.entities.ProductEntity;
import com.imobile3.groovypayments.data.entities.ProductTaxJunctionEntity;
import com.imobile3.groovypayments.data.entities.TaxEntity;
import com.imobile3.groovypayments.data.enums.GroovyColor;
import com.imobile3.groovypayments.data.enums.GroovyIcon;
import com.imobile3.groovypayments.data.utils.CartBuilder;
import com.imobile3.groovypayments.data.utils.CartProductBuilder;
import com.imobile3.groovypayments.data.utils.ProductBuilder;
import com.imobile3.groovypayments.data.utils.TaxBuilder;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class GroovyDemoManager {

    private static final String TAG = GroovyDemoManager.class.getSimpleName();

    //region Singleton Implementation

    private static GroovyDemoManager sInstance;

    private GroovyDemoManager() {
    }

    @NonNull
    public static synchronized GroovyDemoManager getInstance() {
        if (sInstance == null) {
            sInstance = new GroovyDemoManager();
        }
        return sInstance;
    }

    //endregion

    public interface ResetDatabaseCallback {

        void onDatabaseReset();
    }

    /**
     * Delete the current database instance (potentially dangerous operation!)
     * and populate a new instance with fresh demo data.
     */
    public void resetDatabase(
            @NonNull final ResetDatabaseCallback callback) {
        new ResetDatabaseTask(MainApplication.getInstance(), callback)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class ResetDatabaseTask extends AsyncTask<Void, Void, Void> {

        @NonNull
        private final Context mContext;
        @NonNull
        private final ResetDatabaseCallback mCallback;

        private ResetDatabaseTask(
                @NonNull final Context context,
                @NonNull final ResetDatabaseCallback callback) {
            mContext = context;
            mCallback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Blow away any existing database instance.
            DatabaseHelper.getInstance().eraseDatabase(mContext);

            // Initialize a new database instance.
            DatabaseHelper.getInstance().init(mContext);

            List<ProductEntity> productEntities = new ArrayList<>();

            // Add one product.
            productEntities.add(ProductBuilder.build(101L,
                    "Tasty Chicken Sandwich",
                    "Chicken, lettuce, tomato and mayo",
                    750L, 200L,
                    GroovyIcon.Sandwich, GroovyColor.Yellow));

            List<TaxEntity> taxEntities = new ArrayList<>();

            // Add one tax
            taxEntities.add(TaxBuilder.build(101L,
                    "5% Test Tax",
                    "0.05"));

            // Add one TaxProduct

            List<ProductTaxJunctionEntity> productTaxJunctionEntities = new ArrayList<>();
            ProductTaxJunctionEntity productTaxJunctionEntity = new ProductTaxJunctionEntity();

            productTaxJunctionEntity.setProductId(101L);
            productTaxJunctionEntity.setTaxId(101L);

            productTaxJunctionEntities.add(
                    productTaxJunctionEntity);

            // Add one CartEntity
            List<CartEntity> cartEntities = new ArrayList<>();

            cartEntities.add(CartBuilder.build(201L,
                    new Date(180000000L),
                    900L, 100L, 1000L));

            // Add one CartProductEntity
            List<CartProductEntity> cartProductEntities = new ArrayList<>();

            cartProductEntities.add(CartProductBuilder.build(
                    (201L * 2) + 1L,
                    201L,
                    "Tasty Chicken Sandwich",
                    750L,
                    1
            ));

            // Insert entities into database instance.
            DatabaseHelper.getInstance().getDatabase().getProductDao()
                    .insertProducts(
                            productEntities.toArray(new ProductEntity[0]));
            // Insert Tax
            DatabaseHelper.getInstance().getDatabase().getTaxDao()
                    .insertTaxes(taxEntities.toArray(new TaxEntity[0]));

            // Insert TaxProduct
            DatabaseHelper.getInstance().getDatabase().getProductTaxJunctionDao()
                    .insertProductTaxJunctions(
                            productTaxJunctionEntities.toArray(new ProductTaxJunctionEntity[0]));

            // Insert CartEntity
            DatabaseHelper.getInstance().getDatabase().getCartDao()
                    .insertCarts(cartEntities.toArray(new CartEntity[0]));

            // Insert CartProductEntity
            DatabaseHelper.getInstance().getDatabase().getCartProductDao()
                    .insertCartProducts(cartProductEntities.toArray(new CartProductEntity[0]));

            // All done!
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mCallback.onDatabaseReset();
        }
    }
}
