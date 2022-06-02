package com.amazon.tv.tvrecommendations;

import android.graphics.Bitmap;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.RemoteException;

public interface IRecommendationsService extends IInterface {

    abstract class Stub extends Binder implements IRecommendationsService {

        private static class Proxy implements IRecommendationsService {
            private final IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public int getApiVersion() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("IRecommendationsService");
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void registerRecommendationsClient(IRecommendationsClient client, int clientApiVersion) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("IRecommendationsService");
                    _data.writeStrongBinder(client != null ? client.asBinder() : null);
                    _data.writeInt(clientApiVersion);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unregisterRecommendationsClient(IRecommendationsClient client) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("IRecommendationsService");
                    _data.writeStrongBinder(client != null ? client.asBinder() : null);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void registerPartnerRowClient(IRecommendationsClient client, int clientApiVersion) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("IRecommendationsService");
                    _data.writeStrongBinder(client != null ? client.asBinder() : null);
                    _data.writeInt(clientApiVersion);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void unregisterPartnerRowClient(IRecommendationsClient client) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("IRecommendationsService");
                    _data.writeStrongBinder(client != null ? client.asBinder() : null);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void dismissRecommendation(String key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("IRecommendationsService");
                    _data.writeString(key);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public Bitmap getImageForRecommendation(String key) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    Bitmap _result;
                    _data.writeInterfaceToken("IRecommendationsService");
                    _data.writeString(key);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    if (_reply.readInt() != 0) {
                        _result = Bitmap.CREATOR.createFromParcel(_reply);
                    } else {
                        _result = null;
                    }
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } catch (Throwable th) {
                    _reply.recycle();
                    _data.recycle();
                }

                return null;
            }

            public void onActionOpenLaunchPoint(String component, String group) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("IRecommendationsService");
                    _data.writeString(component);
                    _data.writeString(group);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onActionOpenRecommendation(String component, String group) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("IRecommendationsService");
                    _data.writeString(component);
                    _data.writeString(group);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void onActionRecommendationImpression(String component, String group) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("IRecommendationsService");
                    _data.writeString(component);
                    _data.writeString(group);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String[] getRecommendationsPackages() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("IRecommendationsService");
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public String[] getBlacklistedPackages() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("IRecommendationsService");
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    String[] _result = _reply.createStringArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setBlacklistedPackages(String[] blacklist) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken("IRecommendationsService");
                    _data.writeStringArray(blacklist);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, "IRecommendationsService");
        }

        public static IRecommendationsService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface("IRecommendationsService");
            if (!(iin instanceof IRecommendationsService)) {
                return new Proxy(obj);
            }
            return (IRecommendationsService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String[] _result;
            switch (code) {
                case 1:
                    data.enforceInterface("IRecommendationsService");
                    int _result2 = getApiVersion();
                    reply.writeNoException();
                    reply.writeInt(_result2);
                    return true;
                case 2:
                    data.enforceInterface("IRecommendationsService");
                    registerRecommendationsClient(IRecommendationsClient.Stub.asInterface(data.readStrongBinder()), data.readInt());
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface("IRecommendationsService");
                    unregisterRecommendationsClient(IRecommendationsClient.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case 4:
                    data.enforceInterface("IRecommendationsService");
                    registerPartnerRowClient(IRecommendationsClient.Stub.asInterface(data.readStrongBinder()), data.readInt());
                    reply.writeNoException();
                    return true;
                case 5:
                    data.enforceInterface("IRecommendationsService");
                    unregisterPartnerRowClient(IRecommendationsClient.Stub.asInterface(data.readStrongBinder()));
                    reply.writeNoException();
                    return true;
                case 6:
                    data.enforceInterface("IRecommendationsService");
                    dismissRecommendation(data.readString());
                    reply.writeNoException();
                    return true;
                case 7:
                    data.enforceInterface("IRecommendationsService");
                    Bitmap _result3 = getImageForRecommendation(data.readString());
                    reply.writeNoException();
                    if (_result3 != null) {
                        reply.writeInt(1);
                        _result3.writeToParcel(reply, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
                        return true;
                    }
                    reply.writeInt(0);
                    return true;
                case 8:
                    data.enforceInterface("IRecommendationsService");
                    onActionOpenLaunchPoint(data.readString(), data.readString());
                    reply.writeNoException();
                    return true;
                case 9:
                    data.enforceInterface("IRecommendationsService");
                    onActionOpenRecommendation(data.readString(), data.readString());
                    reply.writeNoException();
                    return true;
                case 10:
                    data.enforceInterface("IRecommendationsService");
                    onActionRecommendationImpression(data.readString(), data.readString());
                    reply.writeNoException();
                    return true;
                case 11:
                    data.enforceInterface("IRecommendationsService");
                    _result = getRecommendationsPackages();
                    reply.writeNoException();
                    reply.writeStringArray(_result);
                    return true;
                case 12:
                    data.enforceInterface("IRecommendationsService");
                    _result = getBlacklistedPackages();
                    reply.writeNoException();
                    reply.writeStringArray(_result);
                    return true;
                case 13:
                    data.enforceInterface("IRecommendationsService");
                    setBlacklistedPackages(data.createStringArray());
                    reply.writeNoException();
                    return true;
                case 1598968902:
                    reply.writeString("IRecommendationsService");
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    void dismissRecommendation(String str) throws RemoteException;

    int getApiVersion() throws RemoteException;

    String[] getBlacklistedPackages() throws RemoteException;

    Bitmap getImageForRecommendation(String str) throws RemoteException;

    String[] getRecommendationsPackages() throws RemoteException;

    void onActionOpenLaunchPoint(String str, String str2) throws RemoteException;

    void onActionOpenRecommendation(String str, String str2) throws RemoteException;

    void onActionRecommendationImpression(String str, String str2) throws RemoteException;

    void registerPartnerRowClient(IRecommendationsClient iRecommendationsClient, int i) throws RemoteException;

    void registerRecommendationsClient(IRecommendationsClient iRecommendationsClient, int i) throws RemoteException;

    void setBlacklistedPackages(String[] strArr) throws RemoteException;

    void unregisterPartnerRowClient(IRecommendationsClient iRecommendationsClient) throws RemoteException;

    void unregisterRecommendationsClient(IRecommendationsClient iRecommendationsClient) throws RemoteException;
}
