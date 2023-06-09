package com.example.ecommerceappexample.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerceappexample.R
import com.example.ecommerceappexample.adapters.BestDealsAdapter
import com.example.ecommerceappexample.adapters.BestProductAdapter
import com.example.ecommerceappexample.adapters.SpecialProductsAdapter
import com.example.ecommerceappexample.databinding.FragmentMainCategoryBinding
import com.example.ecommerceappexample.util.Resource
import com.example.ecommerceappexample.viewmodel.SpecialProductsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SpecialProductsFragment : Fragment(R.layout.fragment_main_category) {

    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private val viewModel by viewModels<SpecialProductsViewModel>()
    private lateinit var bestDealsAdapter: BestDealsAdapter
    private lateinit var bestProductsAdapter: BestProductAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductsRecyclerView()
        setupBestDealsRecyclerView()
        setupBestProductsRecyclerView()

        lifecycleScope.launchWhenStarted {
            viewModel.specialProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        specialProductsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e("SpecialProductsFragment", it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestDealsProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        bestDealsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e("SpecialProductsFragment", it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.bestProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        binding.bestProductsProgressBar.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        bestProductsAdapter.differ.submitList(it.data)
                        binding.bestProductsProgressBar.visibility = View.GONE
                    }
                    is Resource.Error -> {
                        Log.e("SpecialProductsFragment", it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_LONG).show()
                        binding.bestProductsProgressBar.visibility = View.GONE
                    }
                    else -> Unit
                }
            }
        }

        binding.nestedScrollMainCategory.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ v,_,scrollY,_,_ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY){
                viewModel.fetchBestProducts()
            }
        })
    }

    private fun setupBestProductsRecyclerView() {
        bestProductsAdapter = BestProductAdapter()
        binding.recyclerViewBestProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }

    private fun setupBestDealsRecyclerView() {
        bestDealsAdapter = BestDealsAdapter()
        binding.recyclerViewBestDealsProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestDealsAdapter
        }
    }

    private fun hideLoading() {
        binding.mainCategoryProgressBar.visibility = View.GONE
    }

    private fun showLoading() {
        binding.mainCategoryProgressBar.visibility = View.VISIBLE
    }

    private fun setupSpecialProductsRecyclerView() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.recyclerViewSpecialProducts.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductsAdapter
        }
    }
}