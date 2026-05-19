package com.example.scam_warming_app.utils

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Kiểm tra số điện thoại có trong danh bạ không
     */
    fun isContactSaved(phoneNumber: String?): Boolean {
        if (phoneNumber.isNullOrEmpty()) return false
        
        val uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )
        
        val projection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
        
        return try {
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                cursor.moveToFirst()
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    /**
     * PHÁT HIỆN GIẢ MẠO:
     * Kiểm tra xem nếu một Tên hiển thị đã tồn tại trong danh bạ với một Số điện thoại khác.
     */
    fun isNameSpoofed(displayName: String?, actualNumber: String?): Boolean {
        if (displayName.isNullOrEmpty() || actualNumber.isNullOrEmpty()) return false
        
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
        val selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} = ?"
        val selectionArgs = arrayOf(displayName)

        return try {
            context.contentResolver.query(uri, projection, selection, selectionArgs, null)?.use { cursor ->
                while (cursor.moveToNext()) {
                    val savedNumber = cursor.getString(0).replace(" ", "").replace("-", "")
                    val normalizedActual = actualNumber.replace(" ", "").replace("-", "")
                    
                    if (savedNumber != normalizedActual && !normalizedActual.endsWith(savedNumber.takeLast(9))) {
                        return true
                    }
                }
                false
            } ?: false
        } catch (e: Exception) {
            false
        }
    }
}
