package ru.skillbranch.devintensive.ui.adapters

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_chat_archive.*
import kotlinx.android.synthetic.main.item_chat_group.*
import kotlinx.android.synthetic.main.item_chat_single.*
import ru.skillbranch.devintensive.R
import ru.skillbranch.devintensive.models.data.ChatItem
import ru.skillbranch.devintensive.models.data.ChatType
import ru.skillbranch.devintensive.ui.archive.ArchiveActivity

class ChatAdapter(private val listener: (ChatItem) -> Unit): RecyclerView.Adapter<ChatAdapter.ChatItemViewHolder>() {

    companion object {
        private const val ARCHIVE_TYPE = 0
        private const val SINGLE_TYPE = 2
        private const val GROUP_TYPE = 3
    }

    var items: List<ChatItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            SINGLE_TYPE -> SingleViewHolder(inflater.inflate(R.layout.item_chat_single, parent, false))
            GROUP_TYPE -> GroupViewHolder(inflater.inflate(R.layout.item_chat_group, parent, false))
            else -> ArchiveItemViewHolder(inflater.inflate(R.layout.item_chat_archive, parent, false))
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ChatItemViewHolder, position: Int) {
        Log.d("M_ChatAdapter", "onBindViewHolder $position")
        holder.bind(items[position], listener)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 && items.any { it.chatType == ChatType.ARCHIVE }) {
            ARCHIVE_TYPE
        } else if (items[position].chatType == ChatType.SINGLE) {
            SINGLE_TYPE
        } else
            GROUP_TYPE
    }

    fun updateData(data: List<ChatItem>) {
        Log.d("M_ChatAdapter", "update data adapter - new data ${data.size} hash: ${data.hashCode()} "
            + "old data ${items.size} hash: ${items.hashCode()}")

        val diffCallback = object: DiffUtil.Callback() {
            override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean = items[oldPos].id == data[newPos].id

            override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean = items[oldPos].hashCode() == data[newPos].hashCode()

            override fun getOldListSize(): Int = items.size

            override fun getNewListSize(): Int = data.size
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items = data
        diffResult.dispatchUpdatesTo(this)
    }

    abstract inner class ChatItemViewHolder(convertView: View): RecyclerView.ViewHolder(convertView), LayoutContainer {
        override val containerView: View?
            get() = itemView

        abstract fun bind(item: ChatItem, listener: (ChatItem) -> Unit)
    }

    inner class SingleViewHolder(convertView: View): ChatItemViewHolder(convertView), ItemTouchViewHolder {

        override fun bind(item: ChatItem, listener: (ChatItem) -> Unit) {
            if (item.avatar == null) {
                Glide.with(itemView).clear(iv_avatar_single)
                iv_avatar_single.setInitials(item.initials)
            } else {
                Glide.with(itemView)
                    .load(item.avatar)
                    .into(iv_avatar_single)
            }
            sv_indicator.visibility = if (item.isOnline) View.VISIBLE else View.GONE
            with(tv_date_single) {
                visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
                text = item.lastMessageDate
            }
            with(tv_counter_single) {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.messageCount.toString()
            }
            tv_title_single.text = item.title
            tv_message_single.text = item.shortDescription
            itemView.setOnClickListener{
                listener.invoke(item)
            }
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemCleared() {
            itemView.setBackgroundColor(Color.WHITE)
        }
    }


    inner class GroupViewHolder(convertView: View): ChatItemViewHolder(convertView), ItemTouchViewHolder {

        @SuppressLint("SetTextI18n")
        override fun bind(item: ChatItem, listener: (ChatItem) -> Unit) {

            iv_avatar_group.setInitials(item.initials)

            with(tv_date_group) {
                visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
                text = item.lastMessageDate
            }
            with(tv_counter_group) {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.messageCount.toString()
            }
            with(tv_message_author) {
                visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
                text = "@" + item.author
            }
            tv_title_group.text = item.title
            tv_message_group.text = item.shortDescription
            itemView.setOnClickListener{
                listener.invoke(item)
            }
        }

        override fun onItemSelected() {
            itemView.setBackgroundColor(Color.LTGRAY)
        }

        override fun onItemCleared() {
            itemView.setBackgroundColor(Color.WHITE)
        }
    }

    inner class ArchiveItemViewHolder(convertView: View): ChatItemViewHolder(convertView), ItemTouchViewHolder {
        @SuppressLint("SetTextI18n")
        override fun bind(item: ChatItem, listener: (ChatItem) -> Unit) {
            tv_message_author_archive.text = "@" + item.author
            tv_message_archive.text = item.shortDescription

            with(tv_date_archive) {
                visibility = if (item.lastMessageDate != null) View.VISIBLE else View.GONE
                text = item.lastMessageDate
            }

            with(tv_counter_archive) {
                visibility = if (item.messageCount > 0) View.VISIBLE else View.GONE
                text = item.messageCount.toString()
            }
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, ArchiveActivity::class.java)
                itemView.context.startActivity(intent)
            }
        }

        override fun onItemSelected() {

        }

        override fun onItemCleared() {

        }

    }
}