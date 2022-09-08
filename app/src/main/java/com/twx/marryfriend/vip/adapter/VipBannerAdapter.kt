package com.twx.marryfriend.vip.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.gifdecoder.GifDecoder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.twx.marryfriend.R
import com.youth.banner.adapter.BannerAdapter


class VipBannerAdapter(imageUrls: List<Int>) :
    BannerAdapter<Int, VipBannerAdapter.ImageHolder>(imageUrls) {

    private lateinit var mContext: Context

    class ImageHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.findViewById(R.id.iv_banner_pic)
    }

    override fun onBindView(holder: ImageHolder, data: Int, position: Int, size: Int) {

        val options = RequestOptions()
            .fitCenter()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.DATA)

        Glide.with(holder.itemView)
            .asGif()
            .load(data)
            .apply(options)
            .listener(object : RequestListener<GifDrawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<GifDrawable>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: GifDrawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<GifDrawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    resource?.setLoopCount(1)
                    return false
                }

            })
            .into(holder.imageView)

    }

    override fun onCreateHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_banner, parent, false)
        mContext = parent.context
        return ImageHolder(view)
    }


}
