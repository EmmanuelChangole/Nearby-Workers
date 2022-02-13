package com.example.nearbyworkers.database
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nearbyworkers.R


class ContactViewAdapter(onItemClick:OnItemClick): ListAdapter<Contact, ContactViewHolder>(DiffCallback())
{
    private var onItemClick: OnItemClick
    init {
        this.onItemClick=onItemClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder
    {
        val inflater = LayoutInflater.from(parent.context)
        return  ContactViewHolder(inflater,parent)

    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int)
    {
        val currentItem=getItem(position)
        holder.bind(currentItem)
        holder.itemView.setOnClickListener(){
         onItemClick.ItemClick(currentItem)
        }


    }


}

class ContactViewHolder(inflater: LayoutInflater, parent: ViewGroup):
    RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item, parent, false))
{
    private var mUsername: TextView? = null
    private var mProfile: ImageView?=null
    init {
        mUsername = itemView.findViewById(R.id.tvUsername)
        mProfile = itemView.findViewById(R.id.imgProfile)
    }

    fun bind(contact:Contact)
    {
        mUsername?.text=contact.name
    }


}


class DiffCallback: DiffUtil.ItemCallback<Contact>(){
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem
    }
}

public interface OnItemClick
{
    fun ItemClick(currentItem: Contact)

}