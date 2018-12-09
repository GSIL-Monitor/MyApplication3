package com.cxy.childstory.utils;

import android.content.Context;
import android.text.TextUtils;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog;

public class DialogUtil {

    private  static QMUITipDialog tipDialog;


    public static final void showResultDialog(Context context, String msg, String title) {
        if(msg == null) return;
        QMUIDialog.MessageDialogBuilder messageDialogBuilder=new QMUIDialog.MessageDialogBuilder(context);
        QMUIDialog dialog;
        messageDialogBuilder.setMessage(msg);
        messageDialogBuilder.setTitle(title);
        // messageDialogBuilder.setLeftAction("确定",null);
        messageDialogBuilder.addAction("知道了", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                if (dialog!=null){
                    dialog.dismiss();
                }
            }
        });

        dialog= messageDialogBuilder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }
    public static final void showResultDialog(Context context, String msg, String title, QMUIDialogAction.ActionListener listener) {
        if(msg == null) return;
        QMUIDialog.MessageDialogBuilder messageDialogBuilder=new QMUIDialog.MessageDialogBuilder(context);
        QMUIDialog dialog;
        messageDialogBuilder.setMessage(msg);
        messageDialogBuilder.setTitle(title);
        // messageDialogBuilder.setLeftAction("确定",null);
        messageDialogBuilder.addAction("知道了", listener);

        dialog= messageDialogBuilder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();

    }

    public static final void showTipDialog(Context context, String message,int iconType) {
        dismissDialog();

        if (TextUtils.isEmpty(message)) {
            message = "正在加载...";
        }
        tipDialog=new QMUITipDialog.Builder(context)
                .setIconType(iconType)
                .setTipWord(message).create();
        tipDialog.show();
        if (iconType!=QMUITipDialog.Builder.ICON_TYPE_LOADING){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                        tipDialog.dismiss();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }



    }

    public static void showConfirmCancelDialog(Context context,
                                               String title, String message,
                                               final QMUIDialogAction.ActionListener listener) {
        QMUIDialog dialog;

        QMUIDialog.MessageDialogBuilder messageDialogBuilder=new QMUIDialog.MessageDialogBuilder(context);
        messageDialogBuilder.setMessage(message);
        messageDialogBuilder.setTitle(title);
        messageDialogBuilder.addAction("取消", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                if (dialog!=null){
                    dialog.dismiss();
                }
            }
        });
        messageDialogBuilder.addAction("确认", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                listener.onClick(dialog,index);
                dialog.dismiss();
            }
        });



        dialog = messageDialogBuilder.create();
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();


      /*  AlertDialog dlg = new AlertDialog.Builder(context).setMessage(message)
                .setPositiveButton("确认", posListener)
                .setNegativeButton("取消", null).create();
        dlg.setCanceledOnTouchOutside(false);
        dlg.show();
        return dlg;*/
        //  return dialog;
    }

    public static final void dismissDialog() {
        if (tipDialog != null) {
            tipDialog.dismiss();
            tipDialog = null;
        }
    }


}
