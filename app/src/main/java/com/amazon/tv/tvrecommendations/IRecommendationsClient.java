package com.amazon.tv.tvrecommendations;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IRecommendationsClient extends IInterface {

    abstract class Stub extends Binder implements IRecommendationsClient {

        private static class Proxy implements IRecommendationsClient {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public void onServiceStatusChanged(boolean isReady) throws RemoteException {
                int i = 1;
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("IRecommendationsClient");
                    if (!isReady) {
                        i = 0;
                    }
                    _data.writeInt(i);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void onClearRecommendations(int reason) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("IRecommendationsClient");
                    _data.writeInt(reason);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void onRecommendationBatchStart() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("IRecommendationsClient");
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void onAddRecommendation(TvRecommendation recommendation) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("IRecommendationsClient");
                    if (recommendation != null) {
                        _data.writeInt(1);
                        recommendation.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void onUpdateRecommendation(TvRecommendation recommendation) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("IRecommendationsClient");
                    if (recommendation != null) {
                        _data.writeInt(1);
                        recommendation.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void onRemoveRecommendation(TvRecommendation recommendation) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("IRecommendationsClient");
                    if (recommendation != null) {
                        _data.writeInt(1);
                        recommendation.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            public void onRecommendationBatchEnd() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("IRecommendationsClient");
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, "IRecommendationsClient");
        }

        public static IRecommendationsClient asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface("IRecommendationsClient");
            if (iin == null || !(iin instanceof IRecommendationsClient)) {
                return new Proxy(obj);
            }
            return (IRecommendationsClient) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            TvRecommendation _arg0;
            switch (code) {
                case 1:
                    data.enforceInterface("IRecommendationsClient");
                    onServiceStatusChanged(data.readInt() != 0);
                    return true;
                case 2:
                    data.enforceInterface("IRecommendationsClient");
                    onClearRecommendations(data.readInt());
                    return true;
                case 3:
                    data.enforceInterface("IRecommendationsClient");
                    onRecommendationBatchStart();
                    return true;
                case 4:
                    data.enforceInterface("IRecommendationsClient");
                    if (data.readInt() != 0) {
                        _arg0 = TvRecommendation.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    onAddRecommendation(_arg0);
                    return true;
                case 5:
                    data.enforceInterface("IRecommendationsClient");
                    if (data.readInt() != 0) {
                        _arg0 = TvRecommendation.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    onUpdateRecommendation(_arg0);
                    return true;
                case 6:
                    data.enforceInterface("IRecommendationsClient");
                    if (data.readInt() != 0) {
                        _arg0 = TvRecommendation.CREATOR.createFromParcel(data);
                    } else {
                        _arg0 = null;
                    }
                    onRemoveRecommendation(_arg0);
                    return true;
                case 7:
                    data.enforceInterface("IRecommendationsClient");
                    onRecommendationBatchEnd();
                    return true;
                case 1598968902:
                    reply.writeString("IRecommendationsClient");
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    void onAddRecommendation(TvRecommendation tvRecommendation) throws RemoteException;

    void onClearRecommendations(int i) throws RemoteException;

    void onRecommendationBatchEnd() throws RemoteException;

    void onRecommendationBatchStart() throws RemoteException;

    void onRemoveRecommendation(TvRecommendation tvRecommendation) throws RemoteException;

    void onServiceStatusChanged(boolean z) throws RemoteException;

    void onUpdateRecommendation(TvRecommendation tvRecommendation) throws RemoteException;
}
