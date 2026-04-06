package com.gp.asset.controller;

import com.gp.asset.entity.GpAsset;
import com.gp.asset.service.GpAssetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class GpAssetController {

    @Autowired
    private GpAssetService service;

    @GetMapping("/all")
    public List<GpAsset> getAllAssets() {
        return service.fetchAllAssets();
    }
}