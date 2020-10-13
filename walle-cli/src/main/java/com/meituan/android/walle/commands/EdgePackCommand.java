package com.meituan.android.walle.commands;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;

import com.meituan.android.walle.ChannelReader;
import com.meituan.android.walle.PayloadWriter;
import com.meituan.android.walle.SignatureNotFoundException;
import com.meituan.android.walle.utils.CommaSeparatedKeyValueConverter;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Parameters(commandDescription = "put channel info into apk")
public class EdgePackCommand implements IWalleCommand{

    // Our Channel Block ID
    public static final int WALLE_BLOCK_ID = 0x71777777;

    @Parameter(required = true, description = "inputFile [outputFile]", arity = 2, converter = FileConverter.class)
    private List<File> files;

    @Parameter(names = {"-k", "--edgepack-id"}, description = "APK sign block ID for EdgePack (default is WALLE's ID)" )
    private int edgepack_id = WALLE_BLOCK_ID;

    @Parameter(names = {"-s", "--edgepack-size"}, description = "APK sign block size for EdgePack" )
    private int edgepack_size = 10240;

    @Override
    public void parse() {
        final File inputFile = files.get(0);
        File outputFile = null;
        if (files.size() == 2) {
            outputFile = files.get(1);
        } else {
            final String name = FilenameUtils.getBaseName(inputFile.getName());
            final String extension = FilenameUtils.getExtension(inputFile.getName());
            final String newName = name + "_edgepack" + "." + extension;
            outputFile = new File(inputFile.getParent(), newName);
        }
        if (!inputFile.equals(outputFile)) {
            try {
                FileUtils.copyFile(inputFile, outputFile);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        String magic = ChannelReader.CDN_MAGIC_KEY;
        int n = edgepack_size - magic.length();
        String edgepack_block = magic + String.format("%"+n+"s","");
        try {
            PayloadWriter.put(outputFile, edgepack_id, edgepack_block, false);
        } catch (IOException | SignatureNotFoundException e) {
            e.printStackTrace();
        }
    }
}
